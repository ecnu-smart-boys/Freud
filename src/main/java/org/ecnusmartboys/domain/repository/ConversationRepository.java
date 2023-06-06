package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.conversation.Comment;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;

import java.util.Date;
import java.util.List;

public interface ConversationRepository {
    PageResult<Conversation> retrieveAllConsultations(Long current, Long size, String name, Long timestamp);

    PageResult<Conversation> retrieveConsultationsByToUser(Long current, Long size, String name, Long timestamp, String toId);

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
     * @param fromId 请求者id
     * @param toId 被请求者id
     */
    String startConsultation(String fromId, String toId);

    String bindHelp(String conversationId, String supervisorId);

    List<Conversation> retrieveConsultationByToId(String toId);

    List<Conversation> retrieveHelpByToId(String toId);

    List<Conversation> retrieveConsultationByFromId(String fromId);


}
