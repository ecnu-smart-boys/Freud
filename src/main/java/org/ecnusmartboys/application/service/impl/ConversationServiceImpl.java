package org.ecnusmartboys.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.doocs.im.constant.SyncOtherMachine;
import io.github.doocs.im.model.message.TIMMsgElement;
import io.github.doocs.im.model.message.TIMTextMsgElement;
import io.github.doocs.im.model.request.SendMsgRequest;
import io.github.doocs.im.model.response.SendMsgResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.ecnusmartboys.application.dto.*;
import org.ecnusmartboys.application.dto.conversation.*;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.ConsultRecordListReq;
import org.ecnusmartboys.application.dto.request.query.OnlineStaffListRequest;
import org.ecnusmartboys.application.dto.response.*;
import org.ecnusmartboys.application.dto.ws.EndConsultationNotification;
import org.ecnusmartboys.application.dto.ws.EndHelpNotification;
import org.ecnusmartboys.application.dto.ws.Notify;
import org.ecnusmartboys.application.service.ConversationService;
import org.ecnusmartboys.domain.model.arrangement.Arrangement;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;
import org.ecnusmartboys.domain.model.conversation.Help;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Consulvisor;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.domain.repository.*;
import org.ecnusmartboys.infrastructure.config.IMConfig;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.infrastructure.exception.BusinessException;
import org.ecnusmartboys.adaptor.ws.WebSocketServer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConversationServiceImpl implements ConversationService {

    private final static long ONE_DAY = 24 * 60 * 60 * 1000L;
    private final ConversationRepository conversationRepository;
    private final ArrangementRepository arrangementRepository;
    private final UserRepository userRepository;
    private final ConsulvisorRepository consulvisorRepository;
    private final OnlineUserRepository onlineUserRepository;
    private final WebSocketServer webSocketServer;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    @Resource
    IMConfig imConfig;

    private final OkHttpClient httpClient;

    @Override
    public Responses<ConsultRecordsResponse> getAllConsultations(ConsultRecordListReq req) {
        var pageResult = conversationRepository.retrieveAllConsultations(req.getCurrent() - 1, req.getSize(), req.getName(), req.getTimestamp());
        var records = convertToConsultationList(pageResult.getData());
        return Responses.ok(new ConsultRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<ConsultRecordsResponse> getConsultConsultations(ConsultRecordListReq req, Common common) {
        var pageResult = conversationRepository.retrieveConsultationsByToUser(req.getCurrent() - 1, req.getSize(), req.getName(), req.getTimestamp(), common.getUserId());
        var records = convertToConsultationList(pageResult.getData());
        return Responses.ok(new ConsultRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<HelpRecordsResponse> getSupervisorHelpRecords(ConsultRecordListReq req, Common common) {
        var pageResult = conversationRepository.retrieveConsultationsByToUser(req.getCurrent() - 1, req.getSize(), req.getName(), req.getTimestamp(), common.getUserId());
        var records = convertToHelpList(pageResult.getData());
        return Responses.ok(new HelpRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<List<WxConsultRecordInfo>> getVisitorConsultations(Common common) {
        List<WxConsultRecordInfo> result = new ArrayList<>();
        long startTime = new Date().getTime();
        var consultations = conversationRepository.retrieveConsultationByVisitorId(common.getUserId());
        long endTime = new Date().getTime();
        log.info("cost time: {} ", endTime - startTime);
        consultations.forEach(conversation -> {
            WxConsultRecordInfo info = new WxConsultRecordInfo();
            info.setConversationId(conversation.getId());
            info.setAvatar(conversation.getToUser().getAvatar()); // 咨询师头像
            info.setStartTime(conversation.getStartTime()); // 开始时间
            info.setEndTime(conversation.getEndTime()); // 结束时间
            info.setConsultantName(conversation.getToUser().getName()); // 咨询师姓名
            info.setScore(conversation.getFromUserComment().getScore());
            info.setState(onlineUserRepository.getConsultantState(conversation.getToUser().getId())); // 咨询师状态
            result.add(info);
        });
        return Responses.ok(result);
    }

    @Override
    public Responses<List<ConversationInfo>> getTodayConsultations() {
        List<ConversationInfo> result = conversationRepository.retrieveByDate(new Date());
        return Responses.ok(result);
    }

    @Override
    public Responses<Integer> getAvgComment(Common common) {
        var conversations = conversationRepository.retrieveHelpByToId(common.getUserId());
        int totalScore = 0;
        int count = 0;
        for (Conversation conversation : conversations) {
            if(conversation.getFromUserComment().getScore() != 0) {
                totalScore += conversation.getFromUserComment().getScore();
                count++;
            }
        }

        if (count == 0) {
            return Responses.ok(0);
        }
        return Responses.ok(totalScore / count);
    }

    @Override
    public Responses<List<DayConsultInfo>> getRecentWeek() {
        long currentDay = (new Date().getTime()) - 6 * ONE_DAY;
        List<DayConsultInfo> dayConsultInfos = new ArrayList<>();

        for (int i = 0; i < 7; i++, currentDay += ONE_DAY) {
            List<ConversationInfo> result = conversationRepository.retrieveByDate(new Date(currentDay));
            dayConsultInfos.add(new DayConsultInfo(currentDay, result.size()));
        }
        return Responses.ok(dayConsultInfos);
    }

    @Override
    public Responses<List<ConversationInfo>> getTodayOwnConsultations(Common common) {
        List<ConversationInfo> result = conversationRepository.retrieveByDateAndToId(new Date(), common.getUserId());
        return Responses.ok(result);
    }

    @Override
    public Responses<ConsultRecordsResponse> getRecentConsultations(Common common) {
        var conversations = conversationRepository.retrieveRecent(common.getUserId());
        var records = convertToConsultationList(conversations);
        return Responses.ok(new ConsultRecordsResponse(records, conversations.size()));
    }

    @Override
    public Responses<ConsultRecordsResponse> getBoundConsultations(ConsultRecordListReq req, Common common) {
        var pageResult = conversationRepository.retrieveBoundConsultations(req.getCurrent() - 1, req.getSize(), req.getName(), req.getTimestamp(), common.getUserId());
        var records = convertToConsultationList(pageResult.getData());
        return Responses.ok(new ConsultRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<HelpRecordsResponse> getRecentHelps(Common common) {
        var conversations = conversationRepository.retrieveRecent(common.getUserId());
        var records = convertToHelpList(conversations);
        return Responses.ok(new HelpRecordsResponse(records, records.size()));
    }

    @Override
    public Responses<List<ConversationInfo>> getTodayHelps(Common common) {
        List<ConversationInfo> result = conversationRepository.retrieveByDateAndToId(new Date(), common.getUserId());
        return Responses.ok(result);
    }

    @Override
    @Transactional
    public Responses<LeftConversation> startConversation(StartConsultRequest req, Common common) {
        // 该咨询师是否在线
        var result = onlineUserRepository.isConsultantOnline(req.getToId());
        if (!result) {
            throw new BadRequestException("该咨询师不在线");
        }

        // 是否已经有一个对话或在排队中
        if (onlineUserRepository.isVisitorBusy(common.getUserId())) {
            throw new BadRequestException("你已经在进行一个会话，或正处于排队等待中");
        }

        // 将该访客插入该咨询师当前咨询队列或等待队列
        if (onlineUserRepository.consultRequest(common.getUserId(), req.getToId())) {
            var consultant = userRepository.retrieveById(req.getToId());
            return Responses.ok(new LeftConversation("-1", consultant.getId(),
                    consultant.getName(), consultant.getAvatar(), false));
        }
        // 创建新咨询会话
        var conversation = conversationRepository.startConsultation(common.getUserId(), req.getToId());
        // 初始化该会话的计时
        onlineUserRepository.resetConversation(conversation.getId());
        // 开始跟踪该会话的消息记录
        onlineUserRepository.addConsultation(conversation.getId(), common.getUserId(), req.getToId());
        // 删除之前的聊天记录
        deleteChatRecords(common.getUserId(), req.getToId());
        deleteChatRecords(req.getToId(), common.getUserId());

        // ws通知咨询师
        LeftConversation notifyConsultant = new LeftConversation(conversation.getId(), conversation.getFromUser().getId(),
                conversation.getFromUser().getName(), conversation.getFromUser().getAvatar(), false);
        var notify = new Notify("start", notifyConsultant);
        try {
            webSocketServer.notifyUser(Long.valueOf(conversation.getToUser().getId()), mapper.writeValueAsString(notify));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LeftConversation notifyVisitor = new LeftConversation(conversation.getId(), conversation.getToUser().getId(),
                conversation.getToUser().getName(), conversation.getToUser().getAvatar(), false);

        return Responses.ok(notifyVisitor);
    }

    @Override
    @Transactional
    public Responses<EndConsultResponse> endConsultation(EndConsultRequest req, Common common) {
        var conversation = conversationRepository.retrieveById(req.getConversationId());

        if (conversation == null || !conversation.isConsultation()) {
            throw new BadRequestException("你要结束的咨询不存在");

        } else if (conversation.getEndTime() != null) {
            throw new BadRequestException("该会话已经结束");

        } else if (!Objects.equals(conversation.getToUser().getId(), common.getUserId())
                && !Objects.equals(conversation.getFromUser().getId(), common.getUserId())) {
            throw new BadRequestException("你不能结束一个未参与的会话");
        }

        // 结束该会话
        endConversation(conversation);

        return Responses.ok("成功结束会话");
    }

    @Override
    public Responses<List<StaffBaseInfo>> getAvailableSupervisors(Common common) {
        var consulvisors = consulvisorRepository.retrieveByConId(common.getUserId());
        // 获得今日排班列表
        Set<String> todayUserId = arrangementRepository.retrieveByDate(new Date())
                .stream()
                .map(Arrangement::getUserId)
                .collect(Collectors.toSet());
        List<StaffBaseInfo> infos = new ArrayList<>();

        Set<String> ids = onlineUserRepository.retrieveAvailableSupervisors(common.getUserId());
        consulvisors.forEach(consulvisor -> {
            if (ids.contains(consulvisor.getSupervisorId()) && todayUserId.contains(consulvisor.getSupervisorId())) {
                User user = userRepository.retrieveById(consulvisor.getSupervisorId());
                infos.add(new StaffBaseInfo(user.getId(), user.getName(), user.getAvatar()));
            }
        });
        return Responses.ok(infos);
    }

    @Override
    @Transactional
    public Responses<Object> callHelp(CallHelpRequest req, Common common) {
        // 首先查看该督导是否在线
        if (!onlineUserRepository.isSupervisorOnline(req.getToId())) {
            throw new BadRequestException("你所要求助的督导不在线");
        }

        // 该督导是否是该咨询师绑定的督导
        boolean bound = false;
        var consulvisors = consulvisorRepository.retrieveBySupId(req.getToId());
        for (var consulvisor : consulvisors) {
            if (Objects.equals(consulvisor.getConsultantId(), common.getUserId())) {
                bound = true;
                break;
            }
        }
        if (!bound) {
            throw new BadRequestException("你未绑定该督导");
        }

        var conversation = conversationRepository.retrieveById(req.getConversationId());
        if (conversation == null || !conversation.isConsultation()) {
            throw new BadRequestException("该咨询记录不存在");

        } else if (!Objects.equals(common.getUserId(), conversation.getToUser().getId())) {
            throw new BadRequestException("你不能给一个未参与的咨询求助督导");

        } else if (conversation.getEndTime() != null) {
            throw new BadRequestException("该会话已经结束");
        }

        if (conversationRepository.retrieveByFromIdAndToId(common.getUserId(), req.getToId()) != null) {
            throw new RuntimeException("你在某个会话中正在求助这个督导，请换一个督导");
        }

        // 将该咨询师插入该督导当前咨询队列或等待队列
        if (onlineUserRepository.callHelpRequest(common.getUserId(), req.getToId(), req.getConversationId())) {
            throw new BadRequestException("该督导忙碌，请换一个督导");
        }

        // 给本次咨询绑定求助会话
        var help = conversationRepository.bindHelp(conversation.getId(), req.getToId());
        // 初始化该会话的计时
        onlineUserRepository.resetConversation(help.getId());
        // 开始跟踪该会话的消息记录
        onlineUserRepository.addHelp(help.getId(), common.getUserId(), req.getToId(), conversation.getFromUser().getId());
        // 删除之前的聊天记录
        deleteChatRecords(common.getUserId(), req.getToId());
        deleteChatRecords(req.getToId(), common.getUserId());

        // ws发消息给督导
        LeftConversation notifySupervisor = new LeftConversation(help.getId(), help.getFromUser().getId(),
                help.getFromUser().getName(), help.getFromUser().getAvatar(), false);
        var notify = new Notify("startHelp", notifySupervisor);
        try {
            webSocketServer.notifyUser(Long.valueOf(help.getToUser().getId()), mapper.writeValueAsString(notify));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LeftConversation notifyConsultant = new LeftConversation(help.getId(), help.getToUser().getId(),
                help.getToUser().getName(), help.getToUser().getAvatar(), false);

        return Responses.ok(notifyConsultant);
    }

    @Override
    public Responses<Object> endHelp(EndHelpRequest req, Common common) {
        var help = conversationRepository.retrieveById(req.getConversationId());
        if (help == null || help.isConsultation()) {
            throw new BadRequestException("该求助会话不存在");

        } else if (help.getEndTime() != null) {
            throw new BadRequestException("你不能关闭一个已经结束的求助会话");

        } else if (!Objects.equals(help.getToUser().getId(), common.getUserId())
                && !Objects.equals(help.getFromUser().getId(), common.getUserId())) {
            throw new BadRequestException("你不能关闭一个没有参与的求助会话");
        }

        endConversation(help);
        // 通知对方
        return Responses.ok("成功结束求助");
    }

    @Override
    public Responses<Object> cancelWaiting(String userId) {
        if (!onlineUserRepository.cancelWaiting(userId)) {
            throw new RuntimeException("你未处于排队状态");
        }
        return Responses.ok("取消排队成功");
    }

    @Override
    public Responses<EndConsultResponse> visitorComment(VisitorCommentRequest req, Common common) {
        var comment = conversationRepository.retrieveComment(req.getConversationId(), common.getUserId());
        // 评论不存在
        if (comment == null) {
            throw new BadRequestException("会话不存在，无法评论");

        } else if (!Objects.equals(comment.getUserId(), common.getUserId())) {
            throw new BadRequestException("你没有资格评论");

        } else if (comment.getCommented()) {
            throw new BadRequestException("该会话你已经评论过了");
        }

        // 包装评论信息
        comment.setCommented(true);
        comment.setText(req.getText());
        comment.setScore(req.getScore());

        conversationRepository.saveComment(comment);
        // ws同步评价内容
        notifyComment(req.getConversationId(), common.getUserId());
        return Responses.ok("评价成功");
    }

    @Override
    public Responses<EndConsultResponse> consultantComment(ConsultantCommentRequest req, Common common) {
        var comment = conversationRepository.retrieveComment(req.getConversationId(), common.getUserId());
        if (comment == null) {
            throw new BadRequestException("会话不存在，无法评论");

        } else if (!Objects.equals(comment.getUserId(), common.getUserId())) {
            throw new BadRequestException("你没有资格评论");

        } else if (comment.getCommented()) {
            throw new BadRequestException("该会话你已经评论过了");
        }

        // 包装评论信息
        comment.setCommented(true);
        comment.setText(req.getText());
        comment.setTag(req.getTag());

        conversationRepository.saveComment(comment);
        // ws同步评价内容
        notifyComment(req.getConversationId(), common.getUserId());
        return Responses.ok("评价成功");
    }

    @Override
    public Responses<Object> setting(SettingRequest req, Common common) {
        if (onlineUserRepository.isStaffInConversation(common.getUserId())) {
            throw new BadRequestException("有会话正在进行中，不可更改咨询设置");
        }

        User user = userRepository.retrieveById(common.getUserId());
        if (user instanceof Consultant) {
            ((Consultant) user).setMaxConversations(req.getMaxConversations());
        } else {
            ((Supervisor) user).setMaxConversations(req.getMaxConversations());
        }
        userRepository.update(user);

        onlineUserRepository.updateSetting(common.getUserId(), req.getMaxConversations());

        return Responses.ok("成功修改咨询设置");
    }

    @Override
    public Responses<RankResponse> getRank() {
        // 获得咨询数量排行
        List<RankUserInfo> list1 = new ArrayList<>();
        var rank1 = conversationRepository.retrieveThisMonthConsultationsInOrder();
        rank1.forEach(rankInfo -> {
            var user = userRepository.retrieveById(rankInfo.getUserId());
            list1.add(new RankUserInfo(user.getAvatar(), user.getName(), rankInfo.getTotal()));
        });

        // 获得好评数量排行
        List<RankUserInfo> list2 = new ArrayList<>();
        var rank2 = conversationRepository.retrieveThisMonthGoodCommentInOrder();
        rank2.forEach(rankInfo -> {
            var user = userRepository.retrieveById(rankInfo.getUserId());
            list2.add(new RankUserInfo(user.getAvatar(), user.getName(), rankInfo.getTotal()));
        });
        return Responses.ok(new RankResponse(list1, list2));
    }

    @Override
    public Responses<Integer> getMaxConversations(Common common) {
        var user = userRepository.retrieveById(common.getUserId());
        if (user instanceof Consultant) {
            return Responses.ok(((Consultant) user).getMaxConversations());
        }

        return Responses.ok(((Supervisor) user).getMaxConversations());
    }

    @Override
    public Responses<OnlineInfoResponse> getOnlineConsultantInfo(OnlineStaffListRequest req) {
        var result = onlineUserRepository.getOnlineConsultantsInfo(req.getCurrent() - 1, req.getSize());
        var staffs = result.getStaffs();
        staffs.forEach(staff -> {
            staff.setName(userRepository.retrieveById(staff.getUserId()).getName());
        });
        return Responses.ok(result);
    }

    @Override
    public Responses<OnlineInfoResponse> getOnlineSupervisorInfo(OnlineStaffListRequest req) {
        var result = onlineUserRepository.getOnlineSupervisorsInfo(req.getCurrent() - 1, req.getSize());
        var staffs = result.getStaffs();
        staffs.forEach(staff -> {
            staff.setName(userRepository.retrieveById(staff.getUserId()).getName());
        });
        return Responses.ok(result);
    }

    @Override
    public Responses<OnlineInfoResponse> getOnlineBoundConsultantInfo(OnlineStaffListRequest req, Common common) {
        List<Consulvisor> consulvisors = consulvisorRepository.retrieveBySupId(common.getUserId());
        Set<String> consultantIds = new HashSet<>();
        consulvisors.forEach(consulvisor -> {
            consultantIds.add(consulvisor.getConsultantId());
        });

        var result = onlineUserRepository.getOnlineBoundConsultantInfo(req.getCurrent() - 1, req.getSize(), consultantIds);
        var staffs = result.getStaffs();
        staffs.forEach(staff -> {
            staff.setName(userRepository.retrieveById(staff.getUserId()).getName());
        });
        return Responses.ok(result);
    }

    @Override
    public Responses<Integer> getOnlineConversationNumber(Common common) {
        User user = userRepository.retrieveById(common.getUserId());

        if(Objects.equals(user.getRole(), Consultant.ROLE)) {
            return Responses.ok(onlineUserRepository.getOnlineConversationNumber(common.getUserId(), user.getRole(), ((Consultant) user).getMaxConversations()));
        }

        return Responses.ok(onlineUserRepository.getOnlineConversationNumber(common.getUserId(), user.getRole(), ((Supervisor) user).getMaxConversations()));
    }

    @Override
    public Responses<List<AvailableConsultant>> getAvailableConsultants(Common common) {
        Set<String> todayUserId = arrangementRepository.retrieveByDate(new Date())
                .stream()
                .map(Arrangement::getUserId)
                .collect(Collectors.toSet());

        List<AvailableConsultant> availableConsultants = new ArrayList<>();
        var response = onlineUserRepository.getOnlineConsultantsInfo(0, 100);
        var consultants = response.getStaffs();
        consultants.forEach(consultant -> {
            if(todayUserId.contains(consultant.getUserId())) {
                AvailableConsultant availableConsultant = new AvailableConsultant();
                availableConsultant.setState(consultant.getState());
                availableConsultant.setConsultantId(consultant.getUserId());

                var user = userRepository.retrieveById(consultant.getUserId());
                availableConsultant.setName(user.getName());
                availableConsultant.setAvatar(user.getAvatar());
                availableConsultant.setHasConsulted(false);

                var consultations = conversationRepository.retrieveConsultationByToId(user.getId());
                int score = 0;
                int count = 0;
                for (var consultation : consultations) {
                    if(consultation.getFromUserComment().getScore() != 0) {
                        count++;
                        score += consultation.getFromUserComment().getScore();
                    }

                    if (Objects.equals(common.getUserId(), consultation.getFromUser().getId())) {
                        availableConsultant.setHasConsulted(true);
                    }
                }

                if(count != 0) {
                    availableConsultant.setAvgComment(score / count);
                } else {
                    availableConsultant.setAvgComment(0);
                }

                availableConsultants.add(availableConsultant);
            }
        });
        return Responses.ok(availableConsultants);
    }

    @Override
    public Responses<WebConversationInfoResponse> getSupervisorOwnHelpInfo(String helpId, Common common) {
        Conversation consultation = conversationRepository.retrieveByHelperId(helpId);
        if (consultation == null || !Objects.equals(consultation.getHelper().getSupervisor().getId(), common.getUserId())) {
            throw new BadRequestException("该督导不存在这条会话记录");
        }

        return Responses.ok(convertToWebConversationInfo(consultation));
    }

    @Override
    public Responses<WebConversationInfoResponse> getBoundConsultantsInfo(String conversationId, Common common) {
        Conversation consultation = conversationRepository.retrieveById(conversationId);
        if (consultation == null || !consultation.isConsultation()) {
            throw new BadRequestException("该咨询记录不存在");

        } else if (consultation.getEndTime() == null) {
            throw new BadRequestException("该会话正在进行中，不可访问");
        }

        var toId = consultation.getToUser().getId();
        List<Consulvisor> consulvisors = consulvisorRepository.retrieveBySupId(common.getUserId());
        List<String> consultantIds = new ArrayList<>();
        consulvisors.forEach(consulvisor -> {
            consultantIds.add(consulvisor.getConsultantId());
        });
        if (!consultantIds.contains(toId)) {
            throw new BadRequestException("该咨询师未绑定，不可查看其咨询详情");
        }

        return Responses.ok(convertToWebConversationInfo(consultation));
    }

    @Override
    public Responses<WebConversationInfoResponse> getConsultantOwnConsultationInfo(String conversationId, Common common) {
        Conversation consultation = conversationRepository.retrieveById(conversationId);
        if (consultation == null || !Objects.equals(consultation.getToUser().getId(), common.getUserId())) {
            throw new BadRequestException("该咨询师不存在该会话消息");

        } else if (consultation.getEndTime() == null) {
            throw new BadRequestException("该会话正在进行中，不可查看其咨询详情");
        }

        return Responses.ok(convertToWebConversationInfo(consultation));
    }

    @Override
    public Responses<WebConversationInfoResponse> getAdminConsultationInfo(String conversationId, Common common) {
        Conversation consultation = conversationRepository.retrieveById(conversationId);
        if (consultation == null || !consultation.isConsultation()) {
            throw new BadRequestException("该咨询记录不存在");

        } else if (consultation.getEndTime() == null) {
            throw new BadRequestException("该咨询尚未结束，无法查看消息记录");
        }

        return Responses.ok(convertToWebConversationInfo(consultation));
    }

    @Override
    public Responses<WxConversationInfoResponse> getVisitorConsultationInfo(String conversationId, Common common) {
        var consultation = conversationRepository.retrieveById(conversationId);
        if(!Objects.equals(consultation.getFromUser().getId(), common.getUserId())) {
            throw new BadRequestException("你不存在该会话");
        }

        WxConversationInfoResponse response = new WxConversationInfoResponse();

        WxConsultationInfo info = new WxConsultationInfo();
        info.setEnd(false);
        info.setUserId(consultation.getId());
        info.setName(consultation.getToUser().getName()); // 咨询师名字
        info.setAvatar(consultation.getToUser().getAvatar()); // 咨询师头像
        info.setStartTime(consultation.getStartTime());

        if(consultation.getEndTime() != null) {
            info.setEndTime(consultation.getEndTime());
            info.setEnd(true);
            response.setVisitorScore(consultation.getFromUserComment().getScore());
            response.setVisitorText(consultation.getFromUserComment().getText());
            response.setConsultantText(consultation.getToUserComment().getText());
            response.setTag(consultation.getToUserComment().getTag());
        }
        return Responses.ok(response);
    }

    @Override
    public Responses<List<LeftConversation>> getConversationsList(Common common) {
        List<LeftConversation> result = new ArrayList<>();
        var conversations = conversationRepository.retrieveConversationListByToId(common.getUserId());
        conversations.forEach(conversation -> {
            var info = new LeftConversation(conversation.getId(), conversation.getFromUser().getId(),
                    conversation.getFromUser().getName(), conversation.getFromUser().getAvatar(), false);
            if (conversation.getEndTime() != null) {
                info.setEnd(true);
            }
            result.add(info);
        });
        return Responses.ok(result);
    }

    @Override
    public Responses<WebConversationInfoResponse> getConsultationInfo(String conversationId, Common common) {
        var consultation = conversationRepository.retrieveById(conversationId);
        if (consultation == null || !Objects.equals(consultation.getToUser().getId(), common.getUserId())) {
            throw new BadRequestException("该咨询师不存在该在线会话");

        }
        return Responses.ok(convertToWebConversationInfo(consultation));
    }

    @Override
    public Responses<WebConversationInfoResponse> getHelpInfo(String helpId, Common common) {
        var consultation = conversationRepository.retrieveByHelperId(helpId);
        if (consultation == null || !Objects.equals(consultation.getHelper().getSupervisor().getId(), common.getUserId())) {
            throw new BadRequestException("该督导不存在该在线会话");

        }

        return Responses.ok(convertToWebConversationInfo(consultation));
    }

    @Override
    public Responses<Object> removeConversation(RemoveConversationRequest req, Common common) {
        var conversation = conversationRepository.retrieveById(req.getConversationId());
        if (conversation == null || !Objects.equals(conversation.getToUser().getId(), common.getUserId())) {
            throw new BadRequestException("你不存在该会话");
        } else if (conversation.getEndTime() == null) {
            throw new BadRequestException("会话尚未结束，不能移除");
        }

        conversation.setShown(true);
        conversationRepository.remove(conversation.getId());
        return Responses.ok("移除成功");
    }

    @Override
    public Responses<OnlineStateResponse> getOnlineVisitorState(Common common) {
        OnlineStateResponse response = new OnlineStateResponse();

        if(!onlineUserRepository.isVisitorBusy(common.getUserId())) {
            // 没有排队和在线会话
            response.setState(0);
            return Responses.ok(response);
        }

        response.setConversation(new LeftConversation());
        response.getConversation().setEnd(false);
        response.setState(2);
        onlineUserRepository.getCurrentConsultant(common.getUserId(), response);

        if(response.getConversation().getConversationId() != null) {
            // 有在线会话
            response.setState(1);
            var conversation = conversationRepository.retrieveById(response.getConversation().getConversationId());
            response.setStartTime(conversation.getStartTime());
        }

        var user= userRepository.retrieveById(response.getConversation().getUserId());
        response.getConversation().setName(user.getName());
        response.getConversation().setAvatar(user.getAvatar());

        return Responses.ok(response);
    }

    @Override
    public Responses<WxConsultationInfo> getCurrentConsultation(Common common) {
        WxConsultationInfo info = new WxConsultationInfo();
        onlineUserRepository.retrieveCurrentConsultationId(common.getUserId(), info);
        if(info.getConversationId() == null) {
            throw new BusinessException(402, "没有在线会话");
        }

        var consultation = conversationRepository.retrieveById(info.getConversationId());
        var consultant = userRepository.retrieveById(info.getUserId());
        info.setName(consultant.getName());
        info.setAvatar(consultant.getAvatar());
        info.setEnd(false);
        info.setStartTime(consultation.getStartTime());
        return Responses.ok(info);
    }

    @NotNull
    private List<ConsultRecordInfo> convertToConsultationList(List<Conversation> conversations) {
        List<ConsultRecordInfo> records = new ArrayList<>();

        conversations.forEach(conversation -> {
            ConsultRecordInfo recordInfo = new ConsultRecordInfo();

            recordInfo.setId(conversation.getId());
            recordInfo.setVisitorName(conversation.getFromUser().getName());
            recordInfo.setConsultantName(conversation.getToUser().getName());
            recordInfo.setScore(conversation.getFromUserComment().getScore());
            recordInfo.setComment(conversation.getFromUserComment().getText());
            recordInfo.setStartTime(conversation.getStartTime());
            recordInfo.setEndTime(conversation.getEndTime());

            if (conversation.getHelper() != null) {
                recordInfo.setHelper(conversation.getHelper().getSupervisor().getName());
            }

            records.add(recordInfo);
        });

        return records;
    }

    private List<HelpRecordInfo> convertToHelpList(List<Conversation> conversations) {
        List<HelpRecordInfo> records = new ArrayList<>();

        conversations.forEach(conversation -> {
            HelpRecordInfo recordInfo = new HelpRecordInfo();

            recordInfo.setId(conversation.getId());
            recordInfo.setConsultantName(conversation.getFromUser().getName());
            recordInfo.setSupervisorName(conversation.getToUser().getName());
            recordInfo.setStartTime(conversation.getStartTime());
            recordInfo.setEndTime(conversation.getEndTime());

            records.add(recordInfo);
        });

        return records;
    }

    /**
     * 将会话类转为会话信息，显示在web端，可以显示督导信息
     */
    private WebConversationInfoResponse convertToWebConversationInfo(Conversation consultation) {
        WebConversationInfoResponse conversationInfoResponse = new WebConversationInfoResponse();
        var toUser = consultation.getToUser();
        var fromUser = consultation.getFromUser();
        ConsultationInfo consultationInfo =
                new ConsultationInfo(consultation.getId(), toUser.getId(), fromUser.getId(), toUser.getName(), toUser.getAvatar(), fromUser.getPhone(),
                        fromUser.getName(), fromUser.getAvatar(), consultation.getStartTime(), System.currentTimeMillis(), false);

        // 会话未结束
        if (consultation.getEndTime() != null) {
            consultationInfo.setLastTime(consultation.getEndTime());
            // 评论
            conversationInfoResponse.setVisitorScore(consultation.getFromUserComment().getScore()); // 评分
            conversationInfoResponse.setVisitorText(consultation.getFromUserComment().getText()); // 访客评价
            conversationInfoResponse.setTag(consultation.getToUserComment().getTag()); // 咨询师评价标签
            conversationInfoResponse.setConsultantText(consultation.getToUserComment().getText()); // 咨询师评价内容

            consultationInfo.setEnd(true);
        }
        conversationInfoResponse.setConsultationInfo(consultationInfo);

        // 求助了督导
        if (consultation.getHelper() != null) {
            conversationInfoResponse.setHelpInfo(convertToHelpInfo(consultation.getHelper()));
        }

        return conversationInfoResponse;
    }

    private HelpInfo convertToHelpInfo(Help help) {
        HelpInfo helpInfo = new HelpInfo(help.getHelpId(), help.getSupervisor().getId(), help.getSupervisor().getAvatar(), help.getSupervisor().getName(),
                help.getStartTime(), System.currentTimeMillis(), false);
        // 求助已经结束
        if (help.getEndTime() != null) {
            helpInfo.setEndTime(help.getEndTime());
            helpInfo.setEnd(true);
        }
        return helpInfo;
    }

    private void updateConsultationQueue(String consultantId) {
        while (true) {
            var visitorId = onlineUserRepository.popFrontConsultation(consultantId);
            if (visitorId == null) {
                // 排队队列为空
                break;
            }

            // 排队期间，咨询师已经下线
            if (!onlineUserRepository.isConsultantOnline(consultantId)) {
                continue;
            }
            // 排队期间，访客等不及了，干脆下线了
            if (!onlineUserRepository.isVisitorOnline(visitorId)) {
                continue;
            }
            // 就是你了
            if (onlineUserRepository.consultRequest(visitorId, consultantId)) {
                // 又要排队了，并发问题，基本不可能
                break;
            }
            var conversation = conversationRepository.startConsultation(visitorId, consultantId);
            // 初始化该会话的计时
            onlineUserRepository.resetConversation(conversation.getId());
            // 开始跟踪该会话的消息记录
            onlineUserRepository.addConsultation(conversation.getId(), visitorId, consultantId);
            // 删除之前的聊天记录
            deleteChatRecords(visitorId, consultantId);
            deleteChatRecords(consultantId, visitorId);

            // ws通知双方会话开始
            LeftConversation notifyConsultant = new LeftConversation(conversation.getId(), conversation.getFromUser().getId(),
                    conversation.getFromUser().getName(), conversation.getFromUser().getAvatar(), false);
            LeftConversation notifyVisitor = new LeftConversation(conversation.getId(), conversation.getToUser().getId(),
                    conversation.getToUser().getName(), conversation.getToUser().getAvatar(), false);
            try {
                // 发送给咨询师
                var notify1 = new Notify("start", notifyConsultant);
                webSocketServer.notifyUser(Long.valueOf(conversation.getToUser().getId()), mapper.writeValueAsString(notify1));
                // 发送给访客
                var notify2 = new Notify("start", notifyVisitor);
                webSocketServer.notifyUser(Long.valueOf(conversation.getFromUser().getId()), mapper.writeValueAsString(notify2));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            break;
        }
    }


    @Scheduled(cron = "*/15 * * * * *")
    public void timeout() {
        var idleConversations = onlineUserRepository.getIdleConversation();
        var timeoutConversations = onlineUserRepository.kickOutTimeoutConversations();

        // 结束
        for (String timeoutConversation : timeoutConversations) {
            var conversation = conversationRepository.retrieveById(timeoutConversation);
            if (conversation == null) {
                // error
                continue;
            }
            log.info("会话 {} 超时结束", conversation.getId());
            endConversation(conversation);
        }

        // TODO 超过10分钟没有产生新的消息的会话，系统会先发送一条确认结束咨询的提示
        idleConversations.forEach(conversationId -> {
            var conversation = conversationRepository.retrieveById(conversationId);
            sendConfirmMsg(conversation.getToUser().getId(), conversation.getFromUser().getId());
            log.info("会话 {} 发送了确认提示消息", conversation.getId());
        });


        // 将这些会话加入到二次机会队列中，5分钟内若没有新的消息，那么会销毁、
        onlineUserRepository.giveSecondChance(idleConversations);
    }

    /**
     * 系统自动发送一条确认结束咨询的提示
     */
    private void sendConfirmMsg(String fromId, String toId) {
        TIMTextMsgElement msg = new TIMTextMsgElement("请问还有什么需要继续咨询的吗？");
        List<TIMMsgElement> msgBody = new ArrayList<>();
        msgBody.add(msg);
        SendMsgRequest request = SendMsgRequest.builder()
                .fromAccount(fromId)
                .toAccount(toId)
                .msgRandom(123L)
                .msgBody(msgBody)
                .syncOtherMachine(SyncOtherMachine.YES)
                .msgTimeStamp((int) (new Date().getTime() / 1000))
                .msgLifeTime(604800)
                .build();
        SendMsgResult result = null;
        try {
            result = imConfig.adminClient().message.sendMsg(request);
            log.info(result.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void endConversation(Conversation conversation) {
        if (!conversation.isConsultation()) {
            // 求助会话
            // 结束求助会话
            conversationRepository.endConversation(conversation.getId());
            // 更新督导当前会话列表
            onlineUserRepository.removeHelp(conversation.getId(), conversation.getFromUser().getId(), conversation.getToUser().getId());
            // 通知督导和咨询师
            notifyEndToUsers(conversation);

        } else {
            // 咨询会话
            // 结束会话，默认添加两个空的评论
            conversationRepository.endConversation(conversation.getId());
            // 求助级联结束
            if (conversation.getHelper() != null) {
                var help = conversationRepository.retrieveById(conversation.getHelper().getHelpId());
                // 结束求助会话
                conversationRepository.endConversation(help.getId());
                // 更新在线用户信息
                onlineUserRepository.removeHelp(help.getId(), help.getFromUser().getId(), help.getToUser().getId());
            }

            // 更新在线用户信息
            onlineUserRepository.removeConsultation(conversation.getId(), conversation.getFromUser().getId(), conversation.getToUser().getId());
            // 通知访客和咨询师
            notifyEndToUsers(conversation);
            updateConsultationQueue(conversation.getToUser().getId());
        }
    }

    private void notifyEndToUsers(Conversation conversation) {
        if (!conversation.isConsultation()) {
            // 求助
            try {
                var consultation = conversationRepository.retrieveByHelperId(conversation.getId());
                EndHelpNotification end = new EndHelpNotification(consultation.getId(), conversation.getId(),
                        conversation.getFromUser().getName(), conversation.getToUser().getName());
                // 发送求助结束信息给咨询师
                var notify = new Notify("endHelp", end);
                webSocketServer.notifyUser(Long.valueOf(conversation.getToUser().getId()), mapper.writeValueAsString(notify));
                // 发送求助结束信息给督导
                webSocketServer.notifyUser(Long.valueOf(conversation.getFromUser().getId()), mapper.writeValueAsString(notify));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // 求助
        try {
            EndConsultationNotification end = new EndConsultationNotification(conversation.getId(), "",
                    conversation.getFromUser().getName(), conversation.getToUser().getName(), "");
            var notify = new Notify("endConsultation", end);
            if (conversation.getHelper() != null) {
                // 求助了督导
                end.setSupervisorName(conversation.getHelper().getSupervisor().getName());
                end.setHelpId(conversation.getHelper().getHelpId());
                // 发送给督导
                webSocketServer.notifyUser(Long.valueOf(conversation.getHelper().getSupervisor().getId()), mapper.writeValueAsString(notify));
            }
            // 发送给咨询师
            webSocketServer.notifyUser(Long.valueOf(conversation.getToUser().getId()), mapper.writeValueAsString(notify));

            // 发送给访客
            var notifyVisitor = new Notify("endConsultation", conversation.getId());
            webSocketServer.notifyUser(Long.valueOf(conversation.getFromUser().getId()), mapper.writeValueAsString(notifyVisitor));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyComment(String conversationId, String userId) {
        var conversation = conversationRepository.retrieveById(conversationId);
        var notify = new Notify("comment", conversation.getId());

        try {

            if (Objects.equals(conversation.getFromUser().getId(), userId)) {
                // 发送给咨询师
                webSocketServer.notifyUser(Long.valueOf(conversation.getToUser().getId()), mapper.writeValueAsString(notify));
            } else {
                // 发送给访客
                webSocketServer.notifyUser(Long.valueOf(conversation.getFromUser().getId()), mapper.writeValueAsString(notify));
            }


            if (conversation.getHelper() != null) {
                // 同步给督导
                webSocketServer.notifyUser(Long.valueOf(conversation.getHelper().getSupervisor().getId()),
                        mapper.writeValueAsString(notify));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteChatRecords(String fromUserId, String toUserId) {
        String baseURL = "https://console.tim.qq.com/v4/recentcontact/delete";
        String identifier = "administrator";
        String random = "99999999";

        try {
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("From_Account", fromUserId);
            requestBody.put("Type", 1);
            requestBody.put("To_Account", toUserId);
            requestBody.put("ClearRamble", 1);

            // 构建请求 URL
            String requestUrl = baseURL + "?sdkappid=" + imConfig.getAppId() + "&identifier=" + identifier +
                    "&usersig=" + imConfig.getUserSig(identifier) + "&random=" + random + "&contenttype=json";
            System.out.println(requestUrl);
            // 创建 URL 对象
            URL url = new URL(requestUrl);

            // 创建 HttpURLConnection 对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 发送请求体
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
