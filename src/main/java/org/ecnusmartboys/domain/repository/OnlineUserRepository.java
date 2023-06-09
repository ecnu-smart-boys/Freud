package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.application.dto.response.OnlineInfoResponse;
import org.ecnusmartboys.domain.model.online.ConsultationInfo;
import org.ecnusmartboys.domain.model.online.HelpInfo;
import org.ecnusmartboys.domain.model.user.User;

import java.util.List;
import java.util.Set;

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

    /**
     * 分页查询当前在线的咨询师状态和总咨询数
     * @param current 页码
     * @param size 页码大小
     */
    OnlineInfoResponse getOnlineConsultantsInfo(long current, long size);

    /**
     * 分页查询当前在线的督导状态和总咨询数
     * @param current 页码
     * @param size 页码大小
     */
    OnlineInfoResponse getOnlineSupervisorsInfo(long current, long size);

    /**
     * 分页查询当前在线且与某督导绑定的咨询师状态和总咨询数
     * @param current 页码
     * @param size 页码大小
     */
    OnlineInfoResponse getOnlineBoundConsultantInfo(long current, long size, Set<String> consultantIds);
}
