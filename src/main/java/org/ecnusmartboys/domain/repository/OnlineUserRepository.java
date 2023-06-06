package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.online.ConsultationInfo;
import org.ecnusmartboys.domain.model.online.HelpInfo;
import org.ecnusmartboys.domain.model.user.User;

import java.util.List;

public interface OnlineUserRepository {

    void Join(User user);

    boolean isConsultantOnline(String consultantId);

    boolean isSupervisorOnline(String supervisorId);

    boolean isVisitorOnline(String visitorId);

    boolean consultationExists(String fromId, String toId);

    boolean consultRequest(String visitorId, String consultantId);

    boolean callHelpRequest(String consultantId, String supervisorId, String conversationId);

    void removeConsultation(String visitorId, String consultantId);

    void removeHelp(String consultantId, String supervisorId);

    HelpInfo popFrontHelp(String supervisorId);

    ConsultationInfo popFrontConsultation(String consultantId);

    /////////////////////////////////////////////////////////////////////////

    void resetConversation(String conversationId);

    void giveSecondChance(List<String> noMsgSend);

    List<String> getIdleConversation();

    List<String> kickOutTimeoutConversations();

    boolean isStaffInConversation(String userId);

    void updateSetting(String userId, Integer maxConversations);
}
