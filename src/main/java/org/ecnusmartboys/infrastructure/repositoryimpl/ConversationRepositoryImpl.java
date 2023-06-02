package org.ecnusmartboys.infrastructure.repositoryimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.model.conversation.Help;
import org.ecnusmartboys.domain.repository.ConversationRepository;
import org.ecnusmartboys.infrastructure.convertor.CommentConvertor;
import org.ecnusmartboys.infrastructure.convertor.ConversationConvertor;
import org.ecnusmartboys.infrastructure.convertor.UserConvertor;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;
import org.ecnusmartboys.infrastructure.data.mysql.table.ConversationDO;
import org.ecnusmartboys.infrastructure.mapper.CommentMapper;
import org.ecnusmartboys.infrastructure.mapper.ConversationMapper;
import org.ecnusmartboys.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ConversationRepositoryImpl implements ConversationRepository {

    private static final Long NULL_HELPER = -1L;

    private final ConversationMapper conversationMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    private final UserConvertor userConvertor;
    private final CommentConvertor commentConvertor;
//    private final ConversationConvertor conversationConvertor;

    @Override
    public PageResult<Conversation> retrieveAllConsultations(Long current, Long size, String name, Long timestamp) {
        List<ConversationDO> conversationDOS = conversationMapper.selectAllConsultation(name, new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp)));
        var conversations = convert(conversationDOS, current, size);
        return new PageResult<>(conversations, conversationDOS.size());
    }

    @Override
    public PageResult<Conversation> retrieveConsultationsByToUser(Long current, Long size, String name, Long timestamp, String toId) {
        List<ConversationDO> conversationDOS = conversationMapper.selectConsultationsByToId(name, new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp)), Long.valueOf(toId));
        var conversations = convert(conversationDOS, current, size);
        return new PageResult<>(conversations, conversationDOS.size());
    }

    @Override
    public List<ConversationInfo> retrieveByDate(Date date) {
        List<ConversationDO> conversationDOS =conversationMapper.selectConsultByDate(new SimpleDateFormat("yyyy-MM-dd").format(date));

        List<ConversationInfo> infos = new ArrayList<>();
        conversationDOS.forEach(conversationDO -> {
            infos.add(new ConversationInfo(conversationDO.getConversationId().toString(), conversationDO.getStartTime().getTime(), conversationDO.getEndTime().getTime()));
        });
        return infos;
    }

    @Override
    public List<ConversationInfo> retrieveByDateAndToId(Date date, String toId) {
        List<ConversationDO> conversationDOS =conversationMapper.selectConsultByDateAndToId(new SimpleDateFormat("yyyy-MM-dd").format(date), toId);

        List<ConversationInfo> infos = new ArrayList<>();
        conversationDOS.forEach(conversationDO -> {
            infos.add(new ConversationInfo(conversationDO.getConversationId().toString(), conversationDO.getStartTime().getTime(), conversationDO.getEndTime().getTime()));
        });
        return infos;
    }

    @Override
    public List<Conversation> retrieveRecent(String toId) {
        List<ConversationDO> conversationDOS = conversationMapper.selectRecentByToId(toId);
        return convert(conversationDOS, 0L, 4L);
    }

    public List<Conversation> convert(List<ConversationDO> conversationDOS, Long current, Long size) {
        List<Conversation> conversations = new ArrayList<>();
        int total = conversationDOS.size();
        for(long i = (current - 1) * size; i < current * size; i++) {
            if(i >= total) {
                break;
            }
            var DO = conversationDOS.get((int) i);
            Conversation conversation = new Conversation();

            conversation.setId(DO.getConversationId().toString());
            conversation.setStartTime(DO.getStartTime().getTime());
            conversation.setEndTime(DO.getStartTime().getTime());

            conversation.setFromUser(userConvertor.toUser(userMapper.selectById(DO.getFromId())));
            conversation.setToUser(userConvertor.toUser(userMapper.selectById(DO.getToId())));
            conversation.setFromUserComment(commentConvertor.toComment(commentMapper.selectByUserAndConId(DO.getFromId(), DO.getConversationId())));
            conversation.setToUserComment(commentConvertor.toComment(commentMapper.selectByUserAndConId(DO.getToId(), DO.getConversationId())));

            if(!Objects.equals(DO.getHelperId(), NULL_HELPER)) {
                var help = conversationMapper.selectById(DO.getHelperId());
                Help helper = new Help(help.getStartTime().getTime(), help.getEndTime().getTime(), userConvertor.toUser(userMapper.selectById(help.getToId())));
                conversation.setHelper(helper);
            }
            conversations.add(conversation);
        }

        return conversations;
    }
}
