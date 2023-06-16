package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.conversation.Comment;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;
import org.ecnusmartboys.domain.model.conversation.RankInfo;

import java.util.Date;
import java.util.List;

public interface ConversationRepository {
    PageResult<Conversation> retrieveAllConsultations(Long current, Long size, String name, Long timestamp);

    PageResult<Conversation> retrieveConsultationsByToUser(Long current, Long size, String name, Long timestamp, String toId);

    PageResult<Conversation> retrieveBoundConsultations(Long current, Long size, String name, Long timestamp, String supervisorId);

    /**
     * 获得该访客已经结束的咨询记录列表
     */
    List<Conversation> retrieveConsultationByVisitorId(String visitorId);

    List<ConversationInfo> retrieveByDate(Date date);

    List<ConversationInfo> retrieveByDateAndToId(Date date, String toId);

    List<Conversation> retrieveRecent(String toId);

    Conversation retrieveById(String conversationId);

    void endConversation(String id);

    Comment retrieveComment(String conversationId, String userId);

    /**
     * 保存评价
     */
    void saveComment(Comment comment);

    /**
     * 开启一个会话
     *
     * @param fromId 请求者id
     * @param toId   被请求者id
     */
    Conversation startConsultation(String fromId, String toId);

    Conversation bindHelp(String conversationId, String supervisorId);

    /**
     * 根据咨询师id，获得所有已完结的咨询会话
     */
    List<Conversation> retrieveConsultationByToId(String toId);

    /**
     * 根据督导/咨询师id，获得所有未被展示的咨询会话
     */
    List<Conversation> retrieveConversationListByToId(String toId);

    /**
     * 根据督导id，获得所有已完结的求助会话
     */
    List<Conversation> retrieveHelpByToId(String toId);

    /**
     * 根据访客id，获得所有已完结的咨询会话
     */
    List<Conversation> retrieveConsultationByFromId(String fromId);

//    /**
//     * 根据访客id，获得所有在线的咨询会话
//     */
//    List<Conversation> retrieveOnlineConsultationByFromId(String fromId); TODO

    /**
     * 获得这个月的咨询会话排名
     */
    List<RankInfo> retrieveThisMonthConsultationsInOrder();

    /**
     * 获得这个月的好评排名
     */
    List<RankInfo> retrieveThisMonthGoodCommentInOrder();

    /**
     * 通过求助督导id获得会话记录
     *
     * @param helperId 求助督导id
     * @return 会话
     */
    Conversation retrieveByHelperId(String helperId);

    /**
     * 通过fromId和toId来确定一个在线的会话
     */
    Conversation retrieveByFromIdAndToId(String fromId, String toId);

    /**
     * 将会话设置为用户已经从列表中移除
     */
    void remove(String conversationId);
}
