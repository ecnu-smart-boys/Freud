package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.message.Message;

public interface MessageRepository {


    PageResult<Message> retrieveByConversationId(String id, long consultationCurrent, long consultationSize);

    void save(Message message);

    void update(Message message);

    /**
     * 通过消息的key获得消息
     */
    Message retrieveByKey(String key);
}
