package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.application.dto.conversation.WxConsultationInfo;
import org.ecnusmartboys.application.dto.response.OnlineInfoResponse;
import org.ecnusmartboys.application.dto.response.OnlineStateResponse;
import org.ecnusmartboys.domain.model.online.ConversationMsgTracker;
import org.ecnusmartboys.domain.model.user.User;

import java.util.List;
import java.util.Set;

public interface OnlineUserRepository {

    /**
     * 用户登录，更新在线用户列表
     */
    void join(User user);

    /**
     * 用户下线，更新在线用户列表
     */
    void logout(String userId);

    /**
     * 判断咨询师是否在线
     */
    boolean isConsultantOnline(String consultantId);

    /**
     * 判断督导是否在线
     */
    boolean isSupervisorOnline(String supervisorId);

    /**
     * 判断访客是否在线
     */
    boolean isVisitorOnline(String visitorId);

    /**
     * 判断访客是否忙碌
     */
    boolean isVisitorBusy(String userId);

    boolean consultRequest(String visitorId, String consultantId);

    boolean callHelpRequest(String consultantId, String supervisorId, String conversationId);

    void removeConsultation(String consultationId, String visitorId, String consultantId);

    void removeHelp(String helpId, String consultantId, String supervisorId);

    String popFrontConsultation(String consultantId);

    /////////////////////////////////////////////////////////////////////////

    void resetConversation(String conversationId);

    void giveSecondChance(List<String> noMsgSend);

    List<String> getIdleConversation();

    List<String> kickOutTimeoutConversations();

    boolean isStaffInConversation(String userId);

    void updateSetting(String userId, Integer maxConversations);

    /**
     * 分页查询当前在线的咨询师状态和总咨询数
     *
     * @param current 页码
     * @param size    页码大小
     */
    OnlineInfoResponse getOnlineConsultantsInfo(long current, long size);

    /**
     * 分页查询当前在线的督导状态和总咨询数
     *
     * @param current 页码
     * @param size    页码大小
     */
    OnlineInfoResponse getOnlineSupervisorsInfo(long current, long size);

    /**
     * 分页查询当前在线且与某督导绑定的咨询师状态和总咨询数
     *
     * @param current 页码
     * @param size    页码大小
     */
    OnlineInfoResponse getOnlineBoundConsultantInfo(long current, long size, Set<String> consultantIds);

    /**
     * 获得咨询师状态
     *
     * @param id 咨询师id
     * @return 0：不在线，1 在线，2 忙碌
     */
    int getConsultantState(String id);

    /**
     * 获得用户在线会话数
     */
    int getOnlineConversationNumber(String userId, String role, int maxConcurrent);

    /**
     * 访客取消排队
     */
    boolean cancelWaiting(String visitorId);

    /**
     * 获得在线且不忙碌的督导集合
     */
    Set<String> retrieveAvailableSupervisors(String consultantId);

    void addConsultation(String conversationId, String fromId, String toId);

    void addHelp(String conversationId, String consultantId, String supervisorId, String visitorId);

    ConversationMsgTracker fetchTracker(String fromAccount, String toAccount);

    /**
     * 在线访客获得当前咨询或排队的咨询师信息
     */
    void getCurrentConsultant(String userId, OnlineStateResponse response);

    /**
     * 访客获得在线会话
     */
    void retrieveCurrentConsultationId(String userId, WxConsultationInfo info);
}
