package org.ecnusmartboys.infrastructure.repositoryimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.dto.OnlineStaffInfo;
import org.ecnusmartboys.application.dto.response.OnlineInfoResponse;
import org.ecnusmartboys.domain.model.online.*;
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

    private final Map<Long, OnlineVisitor> visitorConversations;

    private static final int SEND_NULL_MSG = 2; // TODO
    private static final int END_CONVERSATION = 1; // TODO


    // TODO 可以用redis替换
    private final Map<String, Long> conversations;
    private final Map<String, Long> secondChances;

    public OnlineUserRepositoryImpl() {
        onlineConsultants = new TreeSet<>();
        onlineSupervisors = new TreeSet<>();
        onlineVisitors = new TreeSet<>();
        consultantConversations = new HashMap<>();
        supervisorConversations = new HashMap<>();
        visitorConversations = new HashMap<>();
        conversations = new HashMap<>();
        secondChances = new HashMap<>();
    }


    @Override
    public void join(User user) {
        if(Objects.equals(user.getRole(), Consultant.ROLE)) {
            onlineConsultants.add(Long.valueOf(user.getId()));
            fetchConsultant(Long.parseLong(user.getId())).setMaxConcurrent(((Consultant)user).getMaxConversations());

        } else if(Objects.equals(user.getRole(), Supervisor.ROLE)) {
            onlineSupervisors.add(Long.valueOf(user.getId()));
            fetchSupervisor(Long.parseLong(user.getId())).setMaxConcurrent(((Supervisor)user).getMaxConversations());;

        }  else if(Objects.equals(user.getRole(), Visitor.ROLE)) {
            onlineVisitors.add(Long.valueOf(user.getId()));
            fetchVisitor(Long.parseLong(user.getId()));
        }
    }



    @Override
    public void logout(String userId) {
        try {
            onlineSupervisors.remove(Long.parseLong(userId));
            onlineVisitors.remove(Long.parseLong(userId));
            onlineConsultants.remove(Long.parseLong(userId));
        } catch (NumberFormatException ignored) {

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
    public boolean isVisitorBusy(String userId) {
        var visitor = visitorConversations.get(Long.valueOf(userId));
        return visitor.isBusy();
    }

    @Override
    public boolean consultRequest(String visitorId, String consultantId) {
        var consultant = fetchConsultant(Long.parseLong(consultantId));
        var visitor = fetchVisitor(Long.parseLong(visitorId));
        // 直接咨询
        if(consultant.getVisitors().size() < consultant.getMaxConcurrent()) {
            consultant.getVisitors().add(Long.valueOf(visitorId)); // +
            visitor.startConsultation(Long.parseLong(consultantId)); // +
            return false;
        }

        // 排队
        for(var waitingId : consultant.getWaitingList()) {
            if(Objects.equals(waitingId, visitorId)) {
                // 避免重复排队
                return true;
            }
        }
        consultant.getWaitingList().add(visitorId); // +
        visitor.startWaiting(Long.parseLong(consultantId)); // +

        return true;
    }

    @Override
    public boolean callHelpRequest(String consultantId, String supervisorId, String conversationId) {
        var supervisor = fetchSupervisor(Long.parseLong(supervisorId));
        var consultant = fetchConsultant(Long.parseLong(consultantId));

        // 直接开始求助
        if(supervisor.getConsultants().size() < supervisor.getMaxConcurrent()) {
            supervisor.getConsultants().add(Long.valueOf(consultantId));
            consultant.getSupervisors().add(Long.valueOf(supervisorId));
            return false;
        }

        // 求助人数已满
        return true;
    }

    @Override
    public void removeConsultation(String consultationId, String visitorId, String consultantId) {
        var visitor = fetchVisitor(Long.parseLong(visitorId));
        var consultant = fetchConsultant(Long.parseLong(consultantId));
        // 将访客从咨询集合中删除
        consultant.getVisitors().remove(Long.valueOf(visitorId));
        visitor.endConsultation();

        conversations.remove(consultationId);
        secondChances.remove(consultationId);
    }

    @Override
    public void removeHelp(String helpId, String consultantId, String supervisorId) {
        var supervisor = fetchSupervisor(Long.parseLong(supervisorId));
        var consultant = fetchConsultant(Long.parseLong(consultantId));
        // 将咨询师从求助集合中删除
        supervisor.getConsultants().remove(Long.valueOf(consultantId));
        consultant.getSupervisors().remove(Long.valueOf(supervisorId));

        conversations.remove(helpId);
        secondChances.remove(helpId);
    }

    @Override
    public String popFrontConsultation(String consultantId) {
        var consultant = fetchConsultant(Long.parseLong(consultantId));
        if(consultant.getWaitingList().size() == 0) {
            return null;
        }

        return consultant.getWaitingList().remove(0).toString();
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

    @Override
    public OnlineInfoResponse getOnlineConsultantsInfo(long current, long size) {
        int liveConversations = 0;
        List<OnlineStaffInfo> consultants = new ArrayList<>();
        List<Long> temp = new ArrayList<>(onlineConsultants);
        int total = temp.size();

        for(long i = current * size; i < (current + 1) * size; i++) {
            if(i >= total) {
                break;
            }
            var onlineStaffInfo = new OnlineStaffInfo();
            onlineStaffInfo.setUserId(temp.get((int) i).toString());

            OnlineConsultant consultant = fetchConsultant(temp.get((int) i));
            if(consultant.getVisitors().size() == consultant.getMaxConcurrent()) {
                onlineStaffInfo.setState(2);
                liveConversations += consultant.getVisitors().size();
            }
            consultants.add(onlineStaffInfo);
        }
        return new OnlineInfoResponse(consultants, liveConversations, total);
    }

    @Override
    public OnlineInfoResponse getOnlineSupervisorsInfo(long current, long size) {
        int liveConversations = 0;
        List<OnlineStaffInfo> consultants = new ArrayList<>();
        List<Long> temp = new ArrayList<>(onlineSupervisors);
        int total = temp.size();

        for(long i = current * size; i < (current + 1) * size; i++) {
            if(i >= total) {
                break;
            }
            var onlineStaffInfo = new OnlineStaffInfo();
            onlineStaffInfo.setUserId(temp.get((int) i).toString());

            OnlineSupervisor supervisor = fetchSupervisor(temp.get((int) i));
            if(supervisor.getConsultants().size() == supervisor.getMaxConcurrent()) {
                onlineStaffInfo.setState(2);
                liveConversations += supervisor.getConsultants().size();
            }
            consultants.add(onlineStaffInfo);
        }
        return new OnlineInfoResponse(consultants, liveConversations, total);
    }

    @Override
    public OnlineInfoResponse getOnlineBoundConsultantInfo(long current, long size, Set<String> consultantIds) {
        List<Long> temp = new ArrayList<>();
        for(var consultantId : onlineConsultants) {
            if(consultantIds.contains(consultantId.toString())) {
                temp.add(consultantId);
            }
        }

        List<OnlineStaffInfo> consultants = new ArrayList<>();
        int liveConversations = 0;

        for(long i = current * size; i < (current + 1) * size; i++) {
            if(i >= temp.size()) {
                break;
            }
            var onlineStaffInfo = new OnlineStaffInfo();
            onlineStaffInfo.setUserId(temp.get((int) i).toString());

            OnlineConsultant consultant = fetchConsultant(temp.get((int) i));
            if(consultant.getVisitors().size() == consultant.getMaxConcurrent()) {
                onlineStaffInfo.setState(2);
                liveConversations += consultant.getVisitors().size();
            }
            consultants.add(onlineStaffInfo);
        }
        return new OnlineInfoResponse(consultants, liveConversations, temp.size());
    }

    @Override
    public int getConsultantState(String id) {
        if(!onlineConsultants.contains(Long.valueOf(id))) {
            return 0;
        }

        OnlineConsultant consultant = fetchConsultant(Long.parseLong(id));
        if(consultant.getVisitors().size() == consultant.getMaxConcurrent()) {
            return 2;
        }
        return 1;
    }

    @Override
    public int getOnlineConversationNumber(String userId, String role) {
        if(Objects.equals(role, Consultant.ROLE)) {
            onlineConsultants.add(Long.valueOf(userId)); // 服务器重启后，redis不一致
            OnlineConsultant consultant = fetchConsultant(Long.parseLong(userId));
            return consultant.getVisitors().size();
        }

        onlineSupervisors.add(Long.valueOf(userId));
        OnlineSupervisor supervisor = fetchSupervisor(Long.parseLong(userId));
        return supervisor.getConsultants().size();
    }

    @Override
    public boolean cancelWaiting(String visitorId) {
        var visitor = fetchVisitor(Long.parseLong(visitorId));
        long consultantId = visitor.endWaiting();
        if(consultantId == OnlineVisitor.NULL_CONSULTANT) {
            return false;
        }

        OnlineConsultant consultant = fetchConsultant(consultantId);
        int index = 0;
        for(; index < consultant.getWaitingList().size(); index++) {
            if(Objects.equals(consultant.getWaitingList().get(index), visitorId)) {
                break;
            }
        }

        consultant.getWaitingList().remove(index);
        return true;
    }

    @Override
    public Set<String> retrieveAvailableSupervisors(String consultantId) {
        var consultant = fetchConsultant(Long.parseLong(consultantId));
        var supervisors = consultant.getSupervisors();

        Set<String> result = new HashSet<>();
        onlineSupervisors.forEach(id -> {
            OnlineSupervisor supervisor = fetchSupervisor(id);
            if(supervisor.getConsultants().size() < supervisor.getMaxConcurrent()) {
                result.add(id.toString());
            }
        });

        supervisors.forEach(sId -> {
            result.remove(sId.toString());
        });
        return result;
    }


    private OnlineConsultant fetchConsultant(long userId) {
        if(!consultantConversations.containsKey(userId)) {
            OnlineConsultant consultant = new OnlineConsultant();
            consultantConversations.put(userId, consultant);
        }

        return consultantConversations.get(userId);
    }

    private OnlineSupervisor fetchSupervisor(long userId) {
        if(!supervisorConversations.containsKey(userId)) {
            OnlineSupervisor supervisor = new OnlineSupervisor();
            supervisorConversations.put(userId, supervisor);
        }

        return supervisorConversations.get(userId);
    }

    private OnlineVisitor fetchVisitor(long userId) {
        if(!visitorConversations.containsKey(userId)) {
            visitorConversations.put(userId, new OnlineVisitor());
        }
        return visitorConversations.get(userId);
    }

}


