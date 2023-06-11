package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.data.mysql.intermidium.RankDO;
import org.ecnusmartboys.infrastructure.data.mysql.table.ConversationDO;

import java.util.List;

@Mapper
public interface ConversationMapper extends BaseMapper<ConversationDO> {

    @Select("SELECT a.* FROM " +
            "(SELECT * FROM conversation " +
            "WHERE end_time IS NOT NULL and (#{date} = '1970-01-01' or DATE(start_time) = #{date}) and is_consultation = 0) AS a, " +
            "(SELECT * FROM sys_user WHERE (#{name} = '' or NAME LIKE #{name})) AS b " +
            "WHERE from_id = id;")
    List<ConversationDO> selectAllConsultation(String name, String date);


    @Select("SELECT a.* FROM " +
            "(SELECT * FROM conversation " +
            "WHERE end_time IS NOT NULL and to_id = #{toId} and (#{date} = '1970-01-01' or DATE(start_time) = #{date})) AS a, " +
            "(SELECT * FROM sys_user WHERE (#{name} = '' or NAME LIKE #{name})) AS b " +
            "WHERE from_id = id;")
    List<ConversationDO> selectConsultationsByToId(String name, String date, String toId);

    @Select("SELECT conversation.* FROM " +
            "(SELECT * FROM consulvisor WHERE supervisor_id = #{supervisorId}) " +
            "AS cs JOIN (SELECT NAME, id FROM sys_user WHERE (#{name} = '' or NAME LIKE #{name})) " +
            "AS USER ON cs.consultant_id = user.id," +
            "conversation WHERE to_id = id AND end_time IS NOT NULL and (#{date} = '1970-01-01' or DATE(start_time) = #{date})")
    List<ConversationDO> selectBoundConsultations(String name, String format, String supervisorId);

    @Select("SELECT * from conversation where from_id = #{visitorId} and end_time IS NOT NULL")
    List<ConversationDO> selectConsultationByVisitorId(String visitorId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and DATE(start_time) = #{date} and is_consultation = 0")
    List<ConversationDO> selectConsultByDate(String date);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and DATE(start_time) = #{date} and to_id = #{toId} and is_consultation = 0")
    List<ConversationDO> selectConsultByDateAndToId(String format, String toId);

    @Select("SELECT a.* FROM " +
            "(SELECT * FROM conversation WHERE end_time IS NOT NULL and to_id = #{toId}) AS a, " +
            "(SELECT * FROM sys_user) AS b " +
            "WHERE from_id = id order by start_time desc limit 4")
    List<ConversationDO> selectRecentByToId(String toId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and to_id = #{toId} and is_consultation = 1")
    List<ConversationDO> selectConsultationByToId(String toId);

    @Select("SELECT * FROM conversation where end_time IS NULL and to_id = #{toId}")
    List<ConversationDO> selectOnlineConversationsByToId(String toId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and to_id = #{toId} and is_consultation = 0")
    List<ConversationDO> selectHelpByToId(String toId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NULL and to_id = #{toId} and is_consultation = 0")
    List<ConversationDO> selectOnlineHelpByToId(String toId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and from_id = #{fromId} and is_consultation = 1")
    List<ConversationDO> selectConsultationByFromId(String fromId);


    @Select("SELECT to_id AS user_id, COUNT(*) AS COUNT FROM conversation " +
            "WHERE MONTH(start_time) = #{month} AND end_time IS NOT NULL AND is_consultation = TRUE " +
            "GROUP BY to_id ORDER BY COUNT DESC limit 4;")
    List<RankDO> selectMonthConsultantsInOrder(int month);

    @Select("SELECT to_id, COUNT(*) AS COUNT FROM " +
            "(SELECT * FROM conversation WHERE MONTH(start_time) = 6 AND end_time IS NOT NULL AND is_consultation = TRUE) AS C \n" +
            " ,COMMENT WHERE C.conversation_id = comment.`conversation_id` AND score = 5 AND from_id = user_id " +
            "GROUP BY to_id ORDER BY COUNT DESC LIMIT 4;")
    List<RankDO> selectMonthGoodCommentInOrder(int month);


    @Select("SELECT * from conversation where helper_id = #{helperId}")
    ConversationDO selectByHelperId(String helperId);

    @Select("SELECT * FROM conversation where from_id = #{fromId} and to_id = #{toId} and end_time is NULL")
    ConversationDO selectByFromIdAndToId(String fromId, String toId);
}
