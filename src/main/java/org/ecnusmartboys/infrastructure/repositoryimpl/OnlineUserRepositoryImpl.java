package org.ecnusmartboys.infrastructure.repositoryimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.dto.SupervisorInfo;
import org.ecnusmartboys.domain.model.online.ConsultationInfo;
import org.ecnusmartboys.domain.model.online.HelpInfo;
import org.ecnusmartboys.domain.model.online.OnlineConsultant;
import org.ecnusmartboys.domain.model.online.OnlineSupervisor;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.domain.repository.OnlineUserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class OnlineUserRepositoryImpl implements OnlineUserRepository {

    private final Set<Long> onlineSupervisors;

    private final Set<Long> onlineConsultants;

    private final Set<Long> onlineVisitors;

    private final Map<Long, OnlineConsultant> consultantConversations;

    private final Map<Long, OnlineSupervisor> supervisorConversations;

    private static final int SEND_NULL_MSG = 2; // TODO
    private static final int END_CONVERSATION = 1; // TODO


    // TODO 可以用redis替换
    private final Map<String, Long> conversations;
    private final Map<String, Long> secondChances;

    public OnlineUserRepositoryImpl() {
        onlineConsultants = new HashSet<>();
        onlineSupervisors = new HashSet<>();
        onlineVisitors = new HashSet<>();
        consultantConversations = new HashMap<>();
        supervisorConversations = new HashMap<>();
        conversations = new HashMap<>();
        secondChances = new HashMap<>();
    }


    @Override
    public void Join(User user) {
        if(Objects.equals(user.getRole(), Consultant.ROLE)) {
            onlineConsultants.add(Long.valueOf(user.getId()));
            fetchConsultant(Long.parseLong(user.getId()));

        } else if(Objects.equals(user.getRole(), Supervisor.ROLE)) {
            onlineSupervisors.add(Long.valueOf(user.getId()));
            fetchSupervisor(Long.parseLong(user.getId()));

        }  else if(Objects.equals(user.getRole(), Visitor.ROLE)) {
            onlineVisitors.add(Long.valueOf(user.getId()));
        }
    }

    @Override
    public boolean isConsultantOnline(String consultantId) {
        return onlineConsultants.contains(Long.valueOf(consultantId));
    }

    @Override
    public boolean isSupervisorOnline(String supervisorId) {
        return onlineSupervisors.contains(Long.valueOf(supervisorId));
    }

    @Override
    public boolean isVisitorOnline(String visitorId) {
        return onlineVisitors.contains(Long.valueOf(visitorId));
    }

    @Override
    public boolean consultationExists(String fromId, String toId) {
        var consultant = consultantConversations.get(Long.valueOf(toId));
        return consultant.getVisitors().contains(Long.valueOf(fromId));
    }

    @Override
    public boolean consultRequest(String visitorId, String consultantId) {
        var consultant = fetchConsultant(Long.parseLong(consultantId));
        // 直接咨询
        if(consultant.getVisitors().size() < consultant.getMaxConcurrent()) {
            consultant.getVisitors().add(Long.valueOf(visitorId));
            return false;
        }

        // 排队
        for(ConsultationInfo consultationInfo : consultant.getWaitingList()) {
            if(Objects.equals(consultationInfo.getVisitorId(), visitorId)) {
                // 避免重复排队
                return true;
            }
        }
        consultant.getWaitingList().add(new ConsultationInfo(visitorId, consultantId));

        return true;
    }

    @Override
    public boolean callHelpRequest(String consultantId, String supervisorId, String conversationId) {
        var supervisor = fetchSupervisor(Long.parseLong(supervisorId));

        // 直接开始求助
        if(supervisor.getConsultants().size() < supervisor.getMaxConcurrent()) {
            supervisor.getConsultants().add(Long.valueOf(consultantId));
            return false;
        }

        // 排队
        for(HelpInfo helpInfo : supervisor.getWaitingList()) {
            if(Objects.equals(helpInfo.getConsultantId(), consultantId)) {
                // 避免重复排队
                return true;
            }
        }
        supervisor.getWaitingList().add(new HelpInfo(conversationId, consultantId, supervisorId));
        return true;
    }

    @Override
    public void removeConsultation(String visitorId, String consultantId) {
        var consultant = fetchConsultant(Long.parseLong(consultantId));
        // 将访客从咨询集合中删除
        consultant.getVisitors().remove(Long.valueOf(visitorId));

        conversations.remove(consultantId);
        secondChances.remove(consultantId);
    }

    @Override
    public void removeHelp(String consultantId, String supervisorId) {
        var supervisor = fetchSupervisor(Long.parseLong(supervisorId));
        // 将咨询师从求助集合中删除
        supervisor.getConsultants().remove(Long.valueOf(consultantId));

        conversations.remove(consultantId);
        secondChances.remove(consultantId);
    }

    @Override
    public HelpInfo popFrontHelp(String supervisorId) {
        var supervisor = fetchSupervisor(Long.parseLong(supervisorId));
        if(supervisor.getWaitingList().size() == 0) {
            return null;
        }

        return supervisor.getWaitingList().remove(0);
    }

    @Override
    public ConsultationInfo popFrontConsultation(String consultantId) {
        var consultant = fetchConsultant(Long.parseLong(consultantId));
        if(consultant.getWaitingList().size() == 0) {
            return null;
        }

        return consultant.getWaitingList().remove(0);
    }

    @Override
    public void resetConversation(String conversationId) {
        secondChances.remove(conversationId);
        conversations.put(conversationId, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(SEND_NULL_MSG));
    }

    @Override
    public void giveSecondChance(List<String> conversations) {
        conversations.forEach(conversation -> {
            log.info(conversation + " 被赋予二次机会");
            secondChances.put(conversation, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(END_CONVERSATION));
        });
    }

    @Override
    public List<String> getIdleConversation() {
        List<String> result = new ArrayList<>();

        Iterator<Map.Entry<String, Long>> iterator = conversations.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if(entry.getValue() < System.currentTimeMillis()) {
                result.add(entry.getKey());
                iterator.remove();
            }
        }
        return result;
    }

    @Override
    public List<String> kickOutTimeoutConversations() {
        List<String> result = new ArrayList<>();

        Iterator<Map.Entry<String, Long>> iterator = secondChances.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if(entry.getValue() < System.currentTimeMillis()) {
                result.add(entry.getKey());
                iterator.remove();
            }
        }
        return result;
    }

    @Override
    public boolean isStaffInConversation(String userId) {
        if(consultantConversations.containsKey(Long.valueOf(userId))) {
            var consultant = fetchConsultant(Long.parseLong(userId));
            return consultant.getVisitors().size() != 0;

        } else if(supervisorConversations.containsKey(Long.valueOf(userId))) {
            var supervisor = fetchSupervisor(Long.parseLong(userId));
            return supervisor.getConsultants().size() != 0;
        }

        return false;
    }

    @Override
    public void updateSetting(String userId, Integer maxConversations) {
        if(consultantConversations.containsKey(Long.valueOf(userId))) {
            var consultant = fetchConsultant(Long.parseLong(userId));
            consultant.setMaxConcurrent(maxConversations);

        } else if(supervisorConversations.containsKey(Long.valueOf(userId))) {
            var supervisor = fetchSupervisor(Long.parseLong(userId));
            supervisor.setMaxConcurrent(maxConversations);
        }
    }


    private OnlineConsultant fetchConsultant(long userId) {
        if(!consultantConversations.containsKey(userId)) {
            OnlineConsultant consultant = new OnlineConsultant();
            consultant.setMaxConcurrent(5); // TODO
            consultantConversations.put(userId, consultant);
        }

        return consultantConversations.get(userId);
    }

    private OnlineSupervisor fetchSupervisor(long userId) {
        if(!supervisorConversations.containsKey(userId)) {
            OnlineSupervisor supervisor = new OnlineSupervisor();
            supervisor.setMaxConcurrent(1); // TODO
            supervisorConversations.put(userId, supervisor);
        }

        return supervisorConversations.get(userId);
    }

}


