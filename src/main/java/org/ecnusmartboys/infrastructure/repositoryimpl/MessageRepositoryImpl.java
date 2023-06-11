package org.ecnusmartboys.infrastructure.repositoryimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.message.Message;
import org.ecnusmartboys.domain.repository.MessageRepository;
import org.ecnusmartboys.infrastructure.convertor.MessageConvertor;
import org.ecnusmartboys.infrastructure.data.mysql.table.MessageDO;
import org.ecnusmartboys.infrastructure.mapper.MessageMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageConvertor messageConvertor;
    private final MessageMapper messageMapper;

    @Override
    public PageResult<Message> retrieveByConversationId(String id, long consultationCurrent, long consultationSize) {
        return null;
    }

    @Override
    public void save(Message message) {
        MessageDO messageDO = messageConvertor.toMessageDO(message);
        messageMapper.insert(messageDO);
    }

    @Override
    public void update(Message message) {
        MessageDO messageDO = messageConvertor.toMessageDO(message);
        messageMapper.updateById(messageDO);
    }

    @Override
    public Message retrieveByKey(String key) {
        var messageDO =  messageMapper.selectOne(new LambdaQueryWrapper<MessageDO>().eq(MessageDO::getMsgKey, key));
        if(messageDO == null) {
            return null;
        }
        return messageConvertor.toMessage(messageDO);
    }
}
