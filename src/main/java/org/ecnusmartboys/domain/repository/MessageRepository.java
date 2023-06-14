package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.application.dto.MessageInfo;
import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.message.Message;

import java.util.List;

public interface MessageRepository {


    PageResult<Message> retrieveByConversationId(String id, long consultationCurrent, long consultationSize);

    void save(Message message);

    void update(Message message);

    /**
     * 通过消息的key获得消息
     */
    Message retrieveByKey(String key);

    /**
     * 获得一次会话消息的总条数
     */
    long retrieveTotalByConversationId(String conversationId);

    /**
     * 范围查询得到消息列表
     */
    List<Message> retrieveMsgList(long begin, long end);
}
