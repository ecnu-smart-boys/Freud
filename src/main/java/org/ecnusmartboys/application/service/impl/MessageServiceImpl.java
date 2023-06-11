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
import org.ecnusmartboys.application.dto.request.query.SingleMsgRequest;
import org.ecnusmartboys.application.dto.response.AllMsgListResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.MsgListResponse;
import org.ecnusmartboys.application.service.MessageService;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.model.message.Message;
import org.ecnusmartboys.domain.model.user.Consulvisor;
import org.ecnusmartboys.domain.repository.ConsulvisorRepository;
import org.ecnusmartboys.domain.repository.ConversationRepository;
import org.ecnusmartboys.domain.repository.MessageRepository;
import org.ecnusmartboys.domain.repository.OnlineUserRepository;
import org.ecnusmartboys.infrastructure.config.CosConfig;
import org.ecnusmartboys.infrastructure.data.im.IMCallbackParam;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
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

    private static final String BASE_URL = "https://freud-1311238733.cos.ap-shanghai.myqcloud.com/";

    @Resource
    CosConfig cosConfig;



    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Responses<?> callback(IMCallbackParam param, String body, HttpServletRequest request) {
        try {
            switch (param.getCallbackCommand()) {
                case AFTER_SEND_MSG: {
                    var cb = mapper.readValue(body, AfterSendMsgCallback.class);
                    // 通过from_id 和 to_id获得聊天的会话
                    Conversation conversation = conversationRepository.retrieveByFromIdAndToId(cb.getFromAccount(), cb.getToAccount());
                    if(conversation == null) {
                        // 没有这个在线会话，这是个野消息
                        return Responses.ok();
                    }

                    String msgBody = mapper.writeValueAsString(cb.getMsgBody());
                    String newBody = parseMsgBody(cb.getMsgKey(), msgBody);

                    // 保存消息
                    Message message = new Message();
                    message.setMsgKey(cb.getMsgKey()); // 消息的唯一标识
                    message.setConversationId(conversation.getId());
                    message.setFromId(cb.getFromAccount());
                    message.setToId(cb.getToAccount());
                    message.setMsgBody(newBody); // 消息体
                    message.setRevoked(false);
                    message.setTime(cb.getMsgTime());
                    messageRepository.save(message);

                    // 重置超时计时
                    onlineUserRepository.resetConversation(conversation.getId());

                    // websocket，提醒用户同步消息 TODO

                    break;
                }
                case AFTER_MSG_WITHDRAW: {
                    var cb = mapper.readValue(body, AfterMsgWithdrawCallback.class);
                    var key = cb.getMsgKey(); // 消息的唯一表示
                    Message message = messageRepository.retrieveByKey(key);
                    if(message == null) {
                        break;
                    }
                    message.setRevoked(true);
                    messageRepository.update(message);

                    break;
                }
            }
        } catch (Exception e) {
            log.error("callback exception : ", e);
        }
        return Responses.ok();
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

        // 获得咨询记录
        var consultationResult =  messageRepository.retrieveByConversationId(consultation.getId(), req.getConsultationCurrent(), req.getConsultationSize());
        List<MessageInfo> consultations = convertToInfoList(consultationResult.getData());

        // 获得求助记录
        var helpResult =  messageRepository.retrieveByConversationId(help.getId(), req.getHelpCurrent(), req.getHelpSize());
        List<MessageInfo> helps = convertToInfoList(helpResult.getData());

        return Responses.ok(new AllMsgListResponse(consultations, consultationResult.getTotal(), helps, helpResult.getTotal()));
    }

    @Override
    public Responses<AllMsgListResponse> getBoundConsultantsMsg(AllMessageRequest req, Common common) {
        Conversation consultation = conversationRepository.retrieveById(req.getConversationId());
        if(consultation == null || !consultation.isConsultation()) {
            throw new BadRequestException("该咨询记录不存在");
        }

        if(consultation.getEndTime() == null) {
            throw new BadRequestException("该咨询尚未结束，无法查看消息记录");
        }

        var toId = consultation.getToUser().getId();
        List<Consulvisor> consulvisors = consulvisorRepository.retrieveBySupId(common.getUserId());
        List<String> consultantIds = new ArrayList<>();
        consulvisors.forEach(consulvisor -> {
            consultantIds.add(consulvisor.getConsultantId());
        });
        if(!consultantIds.contains(toId)) {
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

        if(consultation.getEndTime() == null) {
            throw new BadRequestException("该咨询尚未结束，无法查看消息记录");
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

        var consultationResult =  messageRepository.retrieveByConversationId(consultation.getId(), req.getCurrent(), req.getSize());
        List<MessageInfo> consultations = convertToInfoList(consultationResult.getData());

        return Responses.ok(new MsgListResponse(consultations, consultationResult.getTotal()));
    }

    @Override
    public Responses<MsgListResponse> getHelpMsg(SingleMsgRequest req, Common common) {
        Conversation consultation = conversationRepository.retrieveById(req.getConversationId());
        if(consultation == null || !Objects.equals(consultation.getToUser().getId(), common.getUserId())) {
            throw new BadRequestException("你不存在这条会话记录");

        } else if(consultation.getHelper() == null) {
            // 没有求助督导，total返回 -1
            return Responses.ok(new MsgListResponse(null, -1L));
        }

        var consultationResult =  messageRepository.retrieveByConversationId(consultation.getHelper().getHelpId(), req.getCurrent(), req.getSize());
        List<MessageInfo> consultations = convertToInfoList(consultationResult.getData());
        return Responses.ok(new MsgListResponse(consultations, consultationResult.getTotal()));
    }

    private AllMsgListResponse consultationToResponse(AllMessageRequest req, Conversation consultation) {
        // 获得咨询记录
        var consultationResult =  messageRepository.retrieveByConversationId(consultation.getId(), req.getConsultationCurrent(), req.getConsultationSize());
        List<MessageInfo> consultations = convertToInfoList(consultationResult.getData());

        // 没有求助督导
        if(consultation.getHelper() == null) {
            return new AllMsgListResponse(consultations, consultationResult.getTotal(), new ArrayList<>(), 0L);
        }

        // 求助了督导
        var help = conversationRepository.retrieveById(consultation.getHelper().getHelpId());
        var helpResult =  messageRepository.retrieveByConversationId(help.getId(), req.getHelpCurrent(), req.getHelpSize());
        List<MessageInfo> helps = convertToInfoList(helpResult.getData());

        return new AllMsgListResponse(consultations, consultationResult.getTotal(), helps, helpResult.getTotal());
    }


    private List<MessageInfo> convertToInfoList(List<Message> messages) {
        List<MessageInfo> messageInfos = new ArrayList<>();
        messages.forEach(message -> {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setToId(message.getToId());
            messageInfo.setFromId(messageInfo.getFromId());
            messageInfo.setRevoked(messageInfo.isRevoked());

            if(!message.isRevoked()) {
                messageInfo.setMsgBody(message.getMsgBody());
            }
        });
        return messageInfos;
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
                    parseSoundContent(msgKey, index, (SoundElem)messageBodies[index].getMsgContent());
                    continue;
                }

                if(Objects.equals(messageBodies[index].getMsgType(), ImageElem.TYPE)) {
                    // 图片类型，解析为image类型
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
            extension = soundElem.getUrl().substring(lastDotIndex + 1);

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
                extension = imageInfo.getURL().substring(lastDotIndex + 1);

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
