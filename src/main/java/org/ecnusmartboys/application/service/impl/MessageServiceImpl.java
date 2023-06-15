package org.ecnusmartboys.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import io.github.doocs.im.model.callback.AfterMsgWithdrawCallback;
import io.github.doocs.im.model.callback.AfterSendMsgCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.dto.MessageInfo;
import org.ecnusmartboys.application.dto.message.*;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.AllMessageRequest;
import org.ecnusmartboys.application.dto.request.command.SynchronizeMsgRequest;
import org.ecnusmartboys.application.dto.request.query.SingleMsgRequest;
import org.ecnusmartboys.application.dto.response.AllMsgListResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.MsgListResponse;
import org.ecnusmartboys.application.dto.response.SigResponse;
import org.ecnusmartboys.application.dto.ws.MsgNotification;
import org.ecnusmartboys.application.dto.ws.Notify;
import org.ecnusmartboys.application.service.MessageService;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.model.message.Message;
import org.ecnusmartboys.domain.model.online.ConversationMsgTracker;
import org.ecnusmartboys.domain.model.user.Consulvisor;
import org.ecnusmartboys.domain.repository.ConsulvisorRepository;
import org.ecnusmartboys.domain.repository.ConversationRepository;
import org.ecnusmartboys.domain.repository.MessageRepository;
import org.ecnusmartboys.domain.repository.OnlineUserRepository;
import org.ecnusmartboys.infrastructure.config.CosConfig;
import org.ecnusmartboys.infrastructure.config.IMConfig;
import org.ecnusmartboys.infrastructure.data.im.IMCallbackParam;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.infrastructure.ws.WebSocketServer;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.ecnusmartboys.infrastructure.data.im.CallbackCommand.AFTER_MSG_WITHDRAW;
import static org.ecnusmartboys.infrastructure.data.im.CallbackCommand.AFTER_SEND_MSG;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final ConversationRepository conversationRepository;
    private final ConsulvisorRepository consulvisorRepository;
    private final MessageRepository messageRepository;
    private final OnlineUserRepository onlineUserRepository;
    private final WebSocketServer webSocketServer;

    private static final String BASE_URL = "https://freud-1311238733.cos.ap-shanghai.myqcloud.com/";
    private static final long OFFSET = 1L << 32;


    @Resource
    CosConfig cosConfig;

    @Resource
    IMConfig imConfig;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Responses<?> callback(IMCallbackParam param, String body, HttpServletRequest request) {
        try {
            switch (param.getCallbackCommand()) {
                case AFTER_SEND_MSG: {
                    var cb = mapper.readValue(body, AfterSendMsgCallback.class);
                    var tracker = onlineUserRepository.fetchTracker(cb.getFromAccount(), cb.getToAccount());
                    if(tracker == null) {
                        // 没有这个在线会话，这是个野消息
                        return Responses.ok();
                    }

                    long iterator = tracker.increment();

                    String msgBody = mapper.writeValueAsString(cb.getMsgBody());
                    String newBody = parseMsgBody(String.valueOf(iterator) , msgBody);

                    // 保存消息
                    Message message = new Message();
                    message.setMsgKey(cb.getMsgKey()); // 消息标识
                    message.setIterator(iterator);
                    message.setConversationId(tracker.getConversationId());
                    message.setFromId(cb.getFromAccount());
                    message.setToId(cb.getToAccount());
                    message.setMsgBody(newBody); // 消息体
                    message.setRevoked(false);
                    message.setTime(cb.getMsgTime());
                    messageRepository.save(message);

                    // 重置超时计时
                    onlineUserRepository.resetConversation(tracker.getConversationId());

                    // websocket，通知督导同步消息
                    if(tracker.getSupervisorId() != ConversationMsgTracker.NULL_HELP) {
                        MsgNotification msgNotification = new MsgNotification(tracker.getHelpId(), tracker.getConversationId(), convertToInfo(message));
                        Notify notify = new Notify("newMsg", msgNotification);
                        webSocketServer.notifyUser(tracker.getSupervisorId(), mapper.writeValueAsString(notify));
                    }
                    break;
                }
                case AFTER_MSG_WITHDRAW: {
                    var cb = mapper.readValue(body, AfterSendMsgCallback.class);
                    var tracker = onlineUserRepository.fetchTracker(cb.getFromAccount(), cb.getToAccount());
                    if(tracker == null) {
                        // 没有这个在线会话，这是个野消息
                        return Responses.ok();
                    }

                    var key = cb.getMsgKey(); // 消息的唯一表示
                    Message message = messageRepository.retrieveByKey(key);
                    if(message == null) {
                        break;
                    }
                    message.setRevoked(true);
                    messageRepository.update(message);

                    // websocket，通知督导同步消息
                    if(tracker.getSupervisorId() != ConversationMsgTracker.NULL_HELP) {
                        MsgNotification msgNotification = new MsgNotification(tracker.getHelpId(), tracker.getConversationId(), convertToInfo(message));
                        Notify notify = new Notify("revoke", msgNotification);
                        webSocketServer.notifyUser(tracker.getSupervisorId(), mapper.writeValueAsString(notify));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("callback exception : ", e);
        }
        return Responses.ok();
    }

    @Override
    public Responses<SigResponse> generateUserSig(Common common) {
        String sig;
        try {
            sig = imConfig.getUserSig(common.getUserId());
        } catch (Exception e) {
            throw new BadRequestException("生成错误");
        }
        return Responses.ok(new SigResponse(sig));
    }

    @Override
    public Responses<AllMsgListResponse> getSupervisorOwnHelpMsg(AllMessageRequest req, Common common) {
        Conversation help = conversationRepository.retrieveById(req.getConversationId());
        if(help == null || !Objects.equals(help.getToUser().getId(), common.getUserId())) {
            throw new BadRequestException("督导不存在该求助记录");
        }

        Conversation consultation = conversationRepository.retrieveByHelperId(help.getId());
        if(consultation.getEndTime() == null) {
            throw new BadRequestException("该咨询尚未结束，无法查看消息记录");
        }

        return Responses.ok(consultationToResponse(req, consultation));
    }

    @Override
    public Responses<AllMsgListResponse> getBoundConsultantsMsg(AllMessageRequest req, Common common) {
        Conversation consultation = conversationRepository.retrieveById(req.getConversationId());
        if(consultation == null || !consultation.isConsultation()) {
            throw new BadRequestException("该咨询记录不存在");
        }

        List<Consulvisor> consulvisors = consulvisorRepository.retrieveBySupId(common.getUserId());
        List<String> consultantIds = new ArrayList<>();
        consulvisors.forEach(consulvisor -> {
            consultantIds.add(consulvisor.getConsultantId());
        });
        if(!consultantIds.contains(consultation.getToUser().getId())) {
            throw new BadRequestException("该咨询师未绑定，不可查看其咨询详情");
        }

        return Responses.ok(consultationToResponse(req, consultation));
    }

    @Override
    public Responses<AllMsgListResponse> getConsultantOwnConsultationMsg(AllMessageRequest req, Common common) {
        Conversation consultation = conversationRepository.retrieveById(req.getConversationId());
        if(consultation == null || !Objects.equals(consultation.getToUser().getId(), common.getUserId())) {
            throw new BadRequestException("咨询师不存在该咨询记录");
        }

        return Responses.ok(consultationToResponse(req, consultation));
    }

    @Override
    public Responses<AllMsgListResponse> getAdminConsultationMsg(AllMessageRequest req, Common common) {
        Conversation consultation = conversationRepository.retrieveById(req.getConversationId());
        if(consultation == null || !consultation.isConsultation()) {
            throw new BadRequestException("咨询记录不存在，或者还未结束");

        } else if(consultation.getEndTime() == null) {
            throw new BadRequestException("该咨询尚未结束，无法查看消息记录");
        }

        return Responses.ok(consultationToResponse(req, consultation));
    }

    @Override
    public Responses<MsgListResponse> getVisitorConsultationMsg(SingleMsgRequest req, Common common) {
        Conversation consultation = conversationRepository.retrieveById(req.getConversationId());
        if(consultation == null || !Objects.equals(consultation.getFromUser().getId(), common.getUserId())) {
            throw new BadRequestException("你不存在这条会话记录");

        } else if(consultation.getEndTime() == null) {
            throw new BadRequestException("该咨询尚未结束，无法查看消息记录");
        }

        List<MessageInfo> infos = retrieveMsg(consultation.getId(), req.getIterator(), req.getSize());
        return Responses.ok(new MsgListResponse(infos));
    }

    @Override
    public Responses<MsgListResponse> synchronizeConsultationMsg(SynchronizeMsgRequest req, Common common) {
        Conversation consultation = conversationRepository.retrieveById(req.getConversationId());
        if(consultation == null || consultation.getHelper() == null || !Objects.equals(consultation.getHelper().getSupervisor().getId(), common.getUserId())) {
            throw new BadRequestException("该督导不存在此次会话");
        }

        List<MessageInfo> infos = retrieveMsg(consultation.getId(), req.getIterator(), req.getSize());
        return Responses.ok(new MsgListResponse(infos));
    }

    private AllMsgListResponse consultationToResponse(AllMessageRequest req, Conversation consultation) {
        AllMsgListResponse response = new AllMsgListResponse();
        List<MessageInfo> consultationMsg = new ArrayList<>();
        List<MessageInfo> helpMsg = new ArrayList<>();

        if(req.getConsultationIterator() != 0) {
            // 需要获得咨询消息
            consultationMsg = retrieveMsg(consultation.getId(), req.getConsultationIterator(), req.getSize());
        }

        if(consultation.getHelper() != null) {
            response.setCallHelp(true);
            if(req.getHelpIterator() != 0) {
                // 需要获得求助消息
                helpMsg = retrieveMsg(consultation.getHelper().getHelpId(), req.getHelpIterator(), req.getSize());
            }
        } else {
            response.setCallHelp(false);
        }

        response.setConsultation(consultationMsg);
        response.setHelp(helpMsg);


        return response;
    }

    private List<MessageInfo> retrieveMsg(String conversationId, int consultationIterator, long size) {
        long offset = Long.parseLong(conversationId) << 32;
        long end = consultationIterator;

        if(end == -1) {
            end = messageRepository.retrieveTotalByConversationId(conversationId);
        }

        long begin = Math.max(end - size, 0L);
        List<Message> messages = messageRepository.retrieveMsgList(offset + begin, offset + end);
        return convertToInfoList(messages);
    }


    private List<MessageInfo> convertToInfoList(List<Message> messages) {
        List<MessageInfo> messageInfos = new ArrayList<>();
        messages.forEach(message -> {
            messageInfos.add(convertToInfo(message));
        });
        return messageInfos;
    }

    private MessageInfo convertToInfo(Message message) {
        MessageInfo messageInfo = new MessageInfo(); messageInfo.setToId(message.getToId());
        messageInfo.setFromId(message.getFromId());
        messageInfo.setRevoked(message.isRevoked());
        messageInfo.setMsgKey(message.getMsgKey());
        messageInfo.setTime(message.getTime());
        messageInfo.setIterator(message.getIterator() % OFFSET);

        if(!message.isRevoked()) {
            messageInfo.setMsgBody(message.getMsgBody());
        }
        return messageInfo;
    }

    private String parseMsgBody(String msgKey, String msgBody) throws Exception {
        MessageBody[] messageBodies = null;
        try {
            messageBodies = mapper.readValue(msgBody, MessageBody[].class);

            for(int index = 0; index < messageBodies.length; index++) {
                if(Objects.equals(messageBodies[index].getMsgType(), TextElem.TYPE)) {
                    // 文本类型
                    continue;
                }

                if(Objects.equals(messageBodies[index].getMsgType(), SoundElem.TYPE)) {
                    // 语音类型，解析为sound类型
                    parseSoundContent(msgKey, index, (SoundElem) messageBodies[index].getMsgContent());
                    continue;
                }

                if(Objects.equals(messageBodies[index].getMsgType(), ImageElem.TYPE)) {
                    // 图片类型，解析为image类型
                    parseImageContent(msgKey, index, (ImageElem) messageBodies[index].getMsgContent());
                    continue;
                }

                if(Objects.equals(messageBodies[index].getMsgType(), EmojiElem.TYPE)) {
                    // 表情类型
                    continue;
                }

                // 非法消息类型
                throw new RuntimeException("非法消息类型");
            }

            // 重新序列化为json
            return mapper.writeValueAsString(messageBodies);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private void parseSoundContent(String msgKey, int index, SoundElem soundElem) {
        String fileName = msgKey + "_" + index;
        String extension = "";
        try {
            if(Objects.equals(soundElem.getUrl(), "")) {
                return;
            }

            int lastDotIndex = soundElem.getUrl().lastIndexOf('.');
            if (lastDotIndex == -1) { // 不合法url
                throw new IllegalArgumentException("Invalid file URL");
            }
            extension = soundElem.getUrl().substring(lastDotIndex + 1); // 获得后缀名

            // 下载语音文件到本地
            URL url = new URL(soundElem.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 获取输入流
            InputStream inputStream = connection.getInputStream();

            // 创建 PutObjectRequest 对象，并指定输入流和 COS 存储路径
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.cosBucket(), "sound/" + fileName + extension, inputStream, metadata);

            // 执行文件上传
            PutObjectResult putObjectResult = cosConfig.cosClient().putObject(putObjectRequest);
            // 关闭 InputStream
            inputStream.close();

            // 处理上传结果
            if (putObjectResult != null) {
                // 上传成功，修改消息的url
                soundElem.setUrl(BASE_URL + "sound/" + fileName + extension);

            } else {
                // 上传失败，消息url设置为空
                soundElem.setUrl("");

            }
        } catch (JsonProcessingException e) {
            // json反序列化失败
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            // URL 非法，进行异常处理
            throw new IllegalArgumentException("Invalid URL: " + soundElem.getUrl());
        } catch (IOException e) {
            // 其他 IO 异常，进行异常处理
            throw new RuntimeException("Failed to download sound file: " + e.getMessage());
        }
    }

    private void parseImageContent(String msgKey, int index, ImageElem imageElem) {
        String fileName = msgKey + "_" + index;
        String extension = "";
        try {
            for(int i = 0; i < imageElem.getImageInfoArray().size(); i++) {
                var imageInfo = imageElem.getImageInfoArray().get(i);
                if(Objects.equals(imageInfo.getURL(), "")) {
                    continue;
                }

                int lastDotIndex = imageInfo.getURL().lastIndexOf('.');
                if (lastDotIndex == -1) { // 不合法url
                    throw new IllegalArgumentException("Invalid file URL");
                }
                extension = imageInfo.getURL().substring(lastDotIndex + 1); // 获得后缀名

                // 下载图片文件到本地
                URL url = new URL(imageInfo.getURL());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 获取输入流
                InputStream inputStream = connection.getInputStream();

                // 创建 PutObjectRequest 对象，并指定输入流和 COS 存储路径
                String path = "image/" + fileName +"_" + index + extension;
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(inputStream.available());
                PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.cosBucket(), path, inputStream, metadata);

                // 执行文件上传
                PutObjectResult putObjectResult = cosConfig.cosClient().putObject(putObjectRequest);
                // 关闭 InputStream
                inputStream.close();

                // 处理上传结果
                if (putObjectResult != null) {
                    // 上传成功，修改消息的url
                    imageInfo.setURL(BASE_URL + path);

                } else {
                    // 上传失败，消息url设置为空
                    imageInfo.setURL("");
                }
            }
        } catch (MalformedURLException e) {
            // URL 非法，进行异常处理
            throw new IllegalArgumentException("Invalid image URL");
        } catch (IOException e) {
            // 其他 IO 异常，进行异常处理
            throw new RuntimeException("Failed to download sound file: " + e.getMessage());
        }
    }
}
