package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.dto.ConsultRecordInfo;
import org.ecnusmartboys.application.dto.HelpRecordInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.ConsultRecordListReq;
import org.ecnusmartboys.application.dto.response.*;
import org.ecnusmartboys.application.service.ConversationService;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.domain.repository.ConsulvisorRepository;
import org.ecnusmartboys.domain.repository.ConversationRepository;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;
import org.ecnusmartboys.domain.repository.OnlineUserRepository;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConversationServiceImpl implements ConversationService {

    private final static long ONE_DAY = 24 * 60 * 60 * 1000L;

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ConsulvisorRepository consulvisorRepository;
    private final OnlineUserRepository onlineUserRepository;

    @Override
    public Responses<ConsultRecordsResponse> getAllConsultations(ConsultRecordListReq req) {
        var pageResult =  conversationRepository.retrieveAllConsultations(req.getCurrent(), req.getSize(), req.getName(), req.getTimestamp());
        var records = convertToConsultationList(pageResult.getData());
        return Responses.ok(new ConsultRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<ConsultRecordsResponse> getConsultConsultations(ConsultRecordListReq req, Common common) {
        var pageResult =  conversationRepository.retrieveConsultationsByToUser(req.getCurrent(), req.getSize(), req.getName(), req.getTimestamp(), common.getUserId());
        var records = convertToConsultationList(pageResult.getData());
        return Responses.ok(new ConsultRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<HelpRecordsResponse> getSupervisorHelpRecords(ConsultRecordListReq req, Common common) {
        var pageResult = conversationRepository.retrieveConsultationsByToUser(req.getCurrent(), req.getSize(), req.getName(), req.getTimestamp(), common.getUserId());
        var records = convertToHelpList(pageResult.getData());
        return Responses.ok(new HelpRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<List<ConversationInfo>> getTodayConsultations() {
        List<ConversationInfo> result = conversationRepository.retrieveByDate(new Date());
        return Responses.ok(result);
    }

    @Override
    public Responses<List<DayConsultInfo>> getRecentWeek() {
        long currentDay = (new Date().getTime()) - 6 * ONE_DAY;
        List<DayConsultInfo> dayConsultInfos = new ArrayList<>();

        for(int i = 0; i < 7; i++, currentDay += ONE_DAY) {
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
    public Responses<StartConsultResponse> startConversation(StartConsultRequest req, Common common) {
        // 该咨询师是否在线
        var result = onlineUserRepository.isConsultantOnline(req.getToId());
        if(!result) {
            throw new BadRequestException("该咨询师不在线");
        }

        // 是否已经存在一个与该咨询师的会话
        if(onlineUserRepository.consultationExists(common.getUserId(), req.getToId())) {
            throw new BadRequestException("你已经存在一个与该咨询师的会话");
        }

        // 将该访客插入该咨询师当前咨询队列或等待队列
        if(onlineUserRepository.consultRequest(common.getUserId(), req.getToId())) {
            return Responses.ok("请排队等待");
        }
        // 创建新咨询会话
        var conversationId = conversationRepository.startConsultation(common.getUserId(), req.getToId());
        // 初始化该会话的计时
        onlineUserRepository.resetConversation(conversationId);

        return Responses.ok("创建会话成功，可以开始咨询");
    }

    @Override
    @Transactional
    public Responses<EndConsultResponse> endConsultation(EndConsultRequest req, Common common) {
        var conversation = conversationRepository.retrieveById(req.getConversationId());

        if(conversation == null || !conversation.isConsultation()) {
            throw new BadRequestException("你要结束的咨询不存在");

        } else if(conversation.getEndTime() != null) {
            throw new BadRequestException("该会话已经结束");

        } else if(!Objects.equals(conversation.getToUser().getId(), common.getUserId())
                && !Objects.equals(conversation.getFromUser().getId(), common.getUserId())) {
            throw new BadRequestException("你不能结束一个未参与的会话");
        }

        // 结束该会话
        endConversation(conversation);

        return Responses.ok("成功结束会话");
    }

    @Override
    @Transactional
    public Responses<Object>

    callHelp(CallHelpRequest req, Common common) {
        // 首先查看该督导是否在线
        if(!onlineUserRepository.isSupervisorOnline(req.getToId())) {
            throw new BadRequestException("你所要求助的督导不在线");
        }

        // 该督导是否是该咨询师绑定的督导
        boolean bound = false;
        var consulvisors = consulvisorRepository.retrieveBySupId(req.getToId());
        for(var consulvisor : consulvisors) {
            if(Objects.equals(consulvisor.getConsultantId(), common.getUserId())) {
                bound = true;
                break;
            }
        }
        if(!bound) {
            throw new BadRequestException("你未绑定该督导");
        }

        var conversation = conversationRepository.retrieveById(req.getConversationId());
        if(conversation == null || !conversation.isConsultation()) {
            throw new BadRequestException("该咨询记录不存在");

        } else if(!Objects.equals(common.getUserId(), conversation.getToUser().getId())) {
            throw new BadRequestException("你不能给一个未参与的咨询求助督导");

        } else if(conversation.getEndTime() != null) {
            throw new BadRequestException("该会话已经结束");

        } else if(conversation.getHelper() != null) {

            throw new BadRequestException("该会话已经求助了一个督导了");
        }

        // 将该咨询师插入该督导当前咨询队列或等待队列
        if(onlineUserRepository.callHelpRequest(common.getUserId(), req.getToId(), req.getConversationId())) {
            return Responses.ok("请排队等待");
        }

        // 给本次咨询绑定求助会话
        var conversationId = conversationRepository.bindHelp(conversation.getId(), req.getToId());

        // 初始化该会话的计时
        onlineUserRepository.resetConversation(conversationId);

        return Responses.ok("请开始通信");
    }

    @Override
    public Responses<Object> endHelp(EndHelpRequest req, Common common) {
        var help = conversationRepository.retrieveById(req.getConversationId());
        if(help == null || help.isConsultation()) {
            throw new BadRequestException("该求助会话不存在");

        } else if(help.getEndTime() != null) {
            throw new BadRequestException("你不能关闭一个已经结束的求助会话");

        } else if(!Objects.equals(help.getToUser().getId(), common.getUserId())
                && !Objects.equals(help.getFromUser().getId(), common.getUserId())) {
            throw new BadRequestException("你不能关闭一个没有参与的求助会话");
        }

        endConversation(help);
        return Responses.ok("成功结束求助");
    }

    @Override
    public Responses<EndConsultResponse> comment(CommentRequest req, Common common) {
        var comment = conversationRepository.retrieveComment(req.getConversationId(), common.getUserId());
        // 评论不存在
        if(comment == null) {
            throw new BadRequestException("会话不存在，无法评论");

        } else if(!Objects.equals(comment.getUserId(), comment.getId())) {
            throw new BadRequestException("你没有资格评论");

        } else if(comment.getCommented()) {
            throw new BadRequestException("该会话你已经评论过了");
        }

        // 包装评论信息
        comment.setCommented(true);
        comment.setText(req.getText());
        comment.setScore(req.getScore());

        conversationRepository.saveComment(comment);
        return Responses.ok("评价成功");
    }

    @Override
    public Responses<Object> probeConsultation(ProbeRequest req, Common common) {
        if(!onlineUserRepository.isConsultantOnline(req.getConsultantId())) {
            throw new RuntimeException("该咨询师已经下线了");
        }

        if(onlineUserRepository.consultationExists(common.getUserId(), req.getConsultantId())) {
            return Responses.ok("排队结束，请开始聊天");
        }
        return Responses.ok("还在排队中");
    }

    @Override
    public Responses<Object> probeHelp(ProbeRequest req, Common common) {
        // TODO
        return null;
    }

    @Override
    public Responses<Object> setting(SettingRequest req, Common common) {
        if(onlineUserRepository.isStaffInConversation(common.getUserId())) {
            throw new BadRequestException("有会话正在进行中，不可更改咨询设置");
        }

        User user = userRepository.retrieveById(common.getUserId());
        if (user instanceof Consultant) {
            ((Consultant) user).setMaxConversations(req.getMaxConversations());
        } else {
            ((Supervisor) user).setMaxConversations(req.getMaxConversations());
        }
        userRepository.save(user);

        onlineUserRepository.updateSetting(common.getUserId(), req.getMaxConversations());

        return Responses.ok("成功修改咨询设置");
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

            if(conversation.getHelper() != null) {
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

    private void updateHelpQueue(String supervisorId) {
        while (true) {
            var helpInfo = onlineUserRepository.popFrontHelp(supervisorId);
            if(helpInfo == null) {
                // 排队队列为空
                break;
            }

            // 排队期间，督导已经下线
            if(!onlineUserRepository.isSupervisorOnline(helpInfo.getSupervisorId())) {
                continue;
            }
            // 排队期间，咨询师等不及了，已经下线
            if(!onlineUserRepository.isConsultantOnline(helpInfo.getConsultantId())) {
                continue;
            }

            Conversation conversation = conversationRepository.retrieveById(helpInfo.getConversationId());
            // 排队期间，咨询师和访客的会话已经结束，不需要求助了
            if(conversation.getEndTime() != null) {
                continue;
            }
            // 就是你了
            // 将该咨询师插入该督导当前咨询队列或等待队列
            if(onlineUserRepository.callHelpRequest(helpInfo.getConsultantId(), helpInfo.getSupervisorId(), helpInfo.getConversationId())) {
                // 又要排队了，并发问题，基本不可能
                break;
            }

            // 给本次咨询绑定求助会话
            var conversationId = conversationRepository.bindHelp(conversation.getId(), helpInfo.getSupervisorId());
            // 初始化该会话的计时
            onlineUserRepository.resetConversation(conversationId);
        }
    }

    private void updateConsultationQueue(String consultantId) {
        while(true) {
            var consultationInfo = onlineUserRepository.popFrontConsultation(consultantId);
            if(consultationInfo == null) {
                // 排队队列为空
                break;
            }

            // 排队期间，咨询师已经下线
            if(!onlineUserRepository.isConsultantOnline(consultationInfo.getConsultantId())) {
                continue;
            }
            // 排队期间，访客等不及了，干脆下线了 TODO
//            if(!onlineUserRepository.isVisitorOnline(consultationInfo.getVisitorId())) {
//                continue;
//            }
            // 就是你了
            if(onlineUserRepository.consultRequest(consultationInfo.getVisitorId(), consultationInfo.getConsultantId())) {
                // 又要排队了，并发问题，基本不可能
                break;
            }
            var conversationId = conversationRepository.startConsultation(consultationInfo.getVisitorId(), consultationInfo.getConsultantId());
            // 初始化该会话的计时
            onlineUserRepository.resetConversation(conversationId);
        }
    }


    @Scheduled(cron = "*/15 * * * * *")
    public void timeout() {
        var idleConversations = onlineUserRepository.getIdleConversation();
        var timeoutConversations = onlineUserRepository.kickOutTimeoutConversations();

        // 结束
        for(String timeoutConversation : timeoutConversations) {
            var conversation = conversationRepository.retrieveById(timeoutConversation);
            if(conversation == null) {
                // error
                continue;
            }
            log.info("会话 {} 超时结束", conversation.getId());
            endConversation(conversation);
        }

        // TODO 超过10分钟没有产生新的消息的会话，系统会先发送一条确认结束咨询的提示

        // 将这些会话加入到二次机会队列中，5分钟内若没有新的消息，那么会销毁、
        onlineUserRepository.giveSecondChance(idleConversations);
    }

    private void endConversation(Conversation conversation) {
        if(!conversation.isConsultation()) {
            // 求助会话
            // 结束求助会话
            conversationRepository.endConversation(conversation.getId());
            // 更新督导当前会话列表
            onlineUserRepository.removeHelp(conversation.getFromUser().getId(), conversation.getToUser().getId());
            // 将排队队列中的第一个咨询师加到当前队列中
            updateHelpQueue(conversation.getToUser().getId());

        } else {
            // 咨询会话
            // 结束会话，默认添加两个空的评论
            conversationRepository.endConversation(conversation.getId());
            // 求助级联结束
            if(conversation.getHelper() != null) {
                var help = conversationRepository.retrieveById(conversation.getHelper().getHelpId());
                // 结束求助会话
                conversationRepository.endConversation(help.getId());
                // 更新在线用户信息
                onlineUserRepository.removeHelp(help.getFromUser().getId(), help.getToUser().getId());
                // 将排队队列中的第一个咨询师加到当前队列中
                updateHelpQueue(help.getToUser().getId());
            }

            // 更新在线用户信息
            onlineUserRepository.removeConsultation(conversation.getFromUser().getId(), conversation.getToUser().getId());
            updateConsultationQueue(conversation.getToUser().getId());
        }
    }
}
