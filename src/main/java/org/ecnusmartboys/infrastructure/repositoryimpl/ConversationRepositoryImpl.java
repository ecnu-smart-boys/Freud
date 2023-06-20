package org.ecnusmartboys.infrastructure.repositoryimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.conversation.*;
import org.ecnusmartboys.domain.repository.ConversationRepository;
import org.ecnusmartboys.infrastructure.convertor.CommentConvertor;
import org.ecnusmartboys.infrastructure.convertor.UserConvertor;
import org.ecnusmartboys.infrastructure.data.mysql.intermidium.RankDO;
import org.ecnusmartboys.infrastructure.data.mysql.table.CommentDO;
import org.ecnusmartboys.infrastructure.data.mysql.table.ConversationDO;
import org.ecnusmartboys.infrastructure.mapper.CommentMapper;
import org.ecnusmartboys.infrastructure.mapper.ConversationMapper;
import org.ecnusmartboys.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.ecnusmartboys.domain.model.conversation.Conversation.NULL_HELPER;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ConversationRepositoryImpl implements ConversationRepository {


    private final ConversationMapper conversationMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    private final UserConvertor userConvertor;
    private final CommentConvertor commentConvertor;

    @Override
    public PageResult<Conversation> retrieveAllConsultations(Long current, Long size, String name, Long timestamp) {
        List<Conversation> conversations = conversationMapper.selectAllConsultation(name, new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp)));
        return new PageResult<>(conversations.subList((int) (current * size), (int) ((current + 1) * size)), conversations.size());
    }

    @Override
    public PageResult<Conversation> retrieveConsultationsByToUser(Long current, Long size, String name, Long timestamp, String toId) {
        List<Conversation> conversations = conversationMapper.selectConsultationsByToId(name, new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp)), toId);
        return new PageResult<>(conversations.subList((int) (current * size), (int) ((current + 1) * size)), conversations.size());
    }

    @Override
    public PageResult<Conversation> retrieveBoundConsultations(Long current, Long size, String name, Long timestamp, String supervisorId) {
        List<Conversation> conversations = conversationMapper.selectBoundConsultations(name, new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp)), supervisorId);
        return new PageResult<>(conversations.subList((int) (current * size), (int) ((current + 1) * size)), conversations.size());
    }

    @Override
    public List<Conversation> retrieveConsultationByVisitorId(String visitorId) {
        return conversationMapper.selectConsultationByVisitorId(visitorId, 0L, 1L << 31);
    }

    @Override
    public List<ConversationInfo> retrieveByDate(Date date) {
        List<ConversationDO> conversationDOS = conversationMapper.selectConsultByDate(new SimpleDateFormat("yyyy-MM-dd").format(date));

        List<ConversationInfo> infos = new ArrayList<>();
        conversationDOS.forEach(conversationDO -> {
            infos.add(new ConversationInfo(conversationDO.getConversationId().toString(), conversationDO.getStartTime().getTime(), conversationDO.getEndTime().getTime()));
        });
        return infos;
    }

    @Override
    public List<ConversationInfo> retrieveByDateAndToId(Date date, String toId) {
        List<ConversationDO> conversationDOS = conversationMapper.selectConsultByDateAndToId(new SimpleDateFormat("yyyy-MM-dd").format(date), toId);

        List<ConversationInfo> infos = new ArrayList<>();
        conversationDOS.forEach(conversationDO -> {
            infos.add(new ConversationInfo(conversationDO.getConversationId().toString(), conversationDO.getStartTime().getTime(), conversationDO.getEndTime().getTime()));
        });
        return infos;
    }

    @Override
    public List<Conversation> retrieveRecent(String toId) {
        return conversationMapper.selectRecentByToId(toId);
    }

    @Override
    public Conversation retrieveById(String conversationId) {
        ConversationDO conversationDO = conversationMapper.selectById(conversationId);
        if (conversationDO == null) {
            return null;
        }
        return convert(conversationDO);
    }

    @Override
    @Transactional
    public void endConversation(String id) {
        ConversationDO conversationDO = conversationMapper.selectById(id);
        conversationDO.setEndTime(new Date());
        conversationMapper.updateById(conversationDO);

        if (conversationDO.getIsConsultation()) {
            // 是一次咨询，创建两个空评论
            // 访客评论
            CommentDO commentDO1 = new CommentDO();
            commentDO1.setConversationId(conversationDO.getConversationId());
            commentDO1.setUserId(conversationDO.getFromId());

            CommentDO commentDO2 = new CommentDO();
            commentDO2.setConversationId(conversationDO.getConversationId());
            commentDO2.setUserId(conversationDO.getToId());

            commentMapper.insert(commentDO1);
            commentMapper.insert(commentDO2);
        }
    }

    @Override
    public Comment retrieveComment(String conversationId, String userId) {
        var comment = commentMapper.selectOne(new LambdaQueryWrapper<CommentDO>().eq(CommentDO::getConversationId, conversationId)
                .eq(CommentDO::getUserId, userId).eq(CommentDO::getCommented, 0));
        if (comment == null) {
            return null;
        }
        return commentConvertor.toComment(comment);
    }

    @Override
    public void saveComment(Comment comment) {
        CommentDO commentDO = commentConvertor.toCommentDO(comment);
        commentMapper.updateById(commentDO);
    }

    @Override
    public Conversation startConsultation(String fromId, String toId) {
        ConversationDO conversationDO = new ConversationDO();
        conversationDO.setFromId(Long.valueOf(fromId));
        conversationDO.setToId(Long.valueOf(toId));
        conversationDO.setIsConsultation(true);

        conversationMapper.insert(conversationDO);
        return convert(conversationMapper.selectById(conversationDO.getConversationId()));
    }

    @Override
    @Transactional
    public Conversation bindHelp(String conversationId, String supervisorId) {
        ConversationDO conversationDO = conversationMapper.selectById(conversationId);

        ConversationDO helpDO = new ConversationDO();
        helpDO.setFromId(conversationDO.getToId());
        helpDO.setToId(Long.valueOf(supervisorId));
        helpDO.setIsConsultation(false);
        conversationMapper.insert(helpDO);

        conversationDO.setHelperId(helpDO.getConversationId());
        conversationMapper.updateById(conversationDO);
        return convert(conversationMapper.selectById(helpDO.getConversationId()));
    }

    @Override
    public List<Conversation> retrieveConsultationByToId(String toId) {
        return conversationMapper.selectConsultationByToId(toId, 0L, 1L << 32);
    }

    @Override
    public List<Conversation> retrieveConversationListByToId(String toId) {
        return conversationMapper.selectConversationListByToId(toId);
    }

    @Override
    public List<Conversation> retrieveHelpByToId(String toId) {
        return conversationMapper.selectHelpByToId(toId);
    }

    @Override
    public List<Conversation> retrieveConsultationByFromId(String fromId) {
        return  conversationMapper.selectConsultationByFromId(fromId);
    }


    @Override
    public List<RankInfo> retrieveThisMonthConsultationsInOrder() {
        int month = 6; // TODO
        List<RankInfo> result = new ArrayList<>();
        List<RankDO> ranks = conversationMapper.selectMonthConsultantsInOrder(month);
        ranks.forEach(rankDO -> {
            result.add(new RankInfo(rankDO.getUserId().toString(), rankDO.getTotal()));
        });
        return result;
    }

    @Override
    public List<RankInfo> retrieveThisMonthGoodCommentInOrder() {
        int month = 6; // TODO
        List<RankInfo> result = new ArrayList<>();
        List<RankDO> ranks = conversationMapper.selectMonthGoodCommentInOrder(month);
        ranks.forEach(rankDO -> {
            result.add(new RankInfo(rankDO.getUserId().toString(), rankDO.getTotal()));
        });
        return result;
    }

    @Override
    public Conversation retrieveByHelperId(String helperId) {
        ConversationDO conversationDO = conversationMapper.selectByHelperId(helperId);
        if (conversationDO == null) {
            return null;
        }
        return convert(conversationDO);
    }

    @Override
    public Conversation retrieveByFromIdAndToId(String fromId, String toId) {
        ConversationDO conversationDO = conversationMapper.selectByFromIdAndToId(fromId, toId);
        if (conversationDO != null) {
            return convert(conversationDO);
        }

        conversationDO = conversationMapper.selectByFromIdAndToId(toId, fromId);
        if (conversationDO != null) {
            return convert(conversationDO);
        }
        return null;
    }

    @Override
    public void remove(String conversationId) {
        ConversationDO conversationDO = conversationMapper.selectById(conversationId);
        conversationDO.setIsShown(true);
        conversationMapper.updateById(conversationDO);
    }

    private List<Conversation> convert2List(List<ConversationDO> conversationDOS, Long current, Long size) {
        List<Conversation> conversations = new ArrayList<>();
        int total = conversationDOS.size();
        for (long i = current * size; i < (current + 1) * size; i++) {
            if (i >= total) {
                break;
            }
            var DO = conversationDOS.get((int) i);
            Conversation conversation = convert(DO);
            conversations.add(conversation);
        }

        return conversations;
    }

    private Conversation convert(ConversationDO DO) {
        Conversation conversation = new Conversation();

        conversation.setId(DO.getConversationId().toString());
        conversation.setStartTime(DO.getStartTime().getTime());
        if (DO.getEndTime() != null) {
            conversation.setEndTime(DO.getEndTime().getTime());
        }
        conversation.setConsultation(DO.getIsConsultation());

        conversation.setFromUser(userConvertor.toUser(userMapper.selectById(DO.getFromId())));
        conversation.setToUser(userConvertor.toUser(userMapper.selectById(DO.getToId())));
        if (DO.getEndTime() != null) {
            conversation.setFromUserComment(commentConvertor.toComment(commentMapper.selectByUserAndConId(DO.getFromId(), DO.getConversationId())));
            conversation.setToUserComment(commentConvertor.toComment(commentMapper.selectByUserAndConId(DO.getToId(), DO.getConversationId())));
        }

        if (!Objects.equals(DO.getHelperId(), NULL_HELPER)) {
            var help = conversationMapper.selectById(DO.getHelperId());
            Help helper = new Help();
            helper.setHelpId(help.getConversationId().toString());
            helper.setStartTime(help.getStartTime().getTime());
            if (help.getEndTime() != null) {
                helper.setEndTime(help.getEndTime().getTime());
            }
            helper.setSupervisor(userConvertor.toUser(userMapper.selectById(help.getToId())));
            conversation.setHelper(helper);
        }
        return conversation;
    }
}
