package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.PageResult;
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
}
