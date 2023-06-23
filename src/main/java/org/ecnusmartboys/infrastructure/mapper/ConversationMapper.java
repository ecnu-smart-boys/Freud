package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.infrastructure.data.mysql.intermidium.RankDO;
import org.ecnusmartboys.infrastructure.data.mysql.table.ConversationDO;

import java.util.List;

@Mapper
public interface ConversationMapper extends BaseMapper<ConversationDO> {

    String commonSQL =
            "SELECT  c.conversation_id as 'id',  " +
            "        from_user.id AS 'fromUser.id', from_user.name AS 'fromUser.name', from_user.avatar AS 'fromUser.avatar', from_user.phone AS 'fromUser.phone', " +
            "        to_user.id AS 'toUser.id', to_user.name AS 'toUser.name', to_user.avatar AS 'toUser.avatar', to_user.phone AS 'toUser.phone', " +
            "        from_comment.score AS 'fromUserComment.score', from_comment.text AS 'fromUserComment.text', " +
            "        to_comment.text AS 'fromUserComment.text', to_comment.tag AS 'fromUserComment.tag', " +
            "        UNIX_TIMESTAMP(c.start_time) * 1000 AS 'startTime', UNIX_TIMESTAMP(c.end_time) * 1000 AS 'endTime', " +
            "        help.conversation_id AS 'helper.helpId', " +
            "        UNIX_TIMESTAMP(help.start_time) * 1000 AS 'helper.startTime', UNIX_TIMESTAMP(help.end_time) * 1000 AS 'helper.endTime', " +
            "        supervisor.id AS 'helper.supervisor.id', supervisor.name AS 'helper.supervisor.name', supervisor.avatar AS 'helper.supervisor.avatar', " +
            "        c.is_consultation, c.is_shown " +
            "FROM conversations c " +
            "JOIN sys_user from_user ON c.from_id = from_user.id " +
            "JOIN sys_user to_user ON c.to_id = to_user.id " +
            "LEFT JOIN ecnu.comment from_comment ON from_comment.user_id = c.from_id AND from_comment.conversation_id = c.conversation_id " +
            "LEFT JOIN ecnu.comment to_comment ON to_comment.user_id = c.to_id AND to_comment.conversation_id = c.conversation_id " +
            "LEFT JOIN conversation help ON help.conversation_id = c.helper_id " +
            "LEFT JOIN sys_user supervisor ON supervisor.id = help.to_id; ";

    @Select("WITH conversations AS ( " +
            "SELECT a.* FROM " +
            "(SELECT * FROM conversation " +
            "WHERE end_time IS NOT NULL and (#{date} = '1970-01-01' or DATE(start_time) = #{date}) and is_consultation = 1) AS a, " +
            "(SELECT * FROM sys_user WHERE (#{name} = '' or name LIKE CONCAT('%', #{name}, '%'))) AS b " +
            "WHERE from_id = id order by start_time desc ) "
            + commonSQL)
    List<Conversation> selectAllConsultation(String name, String date);


    @Select("WITH conversations AS ( " +
            "SELECT a.* FROM " +
            "(SELECT * FROM conversation " +
            "WHERE end_time IS NOT NULL and to_id = #{toId} and (#{date} = '1970-01-01' or DATE(start_time) = #{date})) AS a, " +
            "(SELECT * FROM sys_user WHERE (#{name} = '' or name LIKE CONCAT('%', #{name}, '%'))) AS b " +
            "WHERE from_id = id order by start_time desc ) " +
            commonSQL)
    List<Conversation> selectConsultationsByToId(String name, String date, String toId);

    @Select("WITH conversations AS ( " +
            "SELECT b.* FROM  " +
            "(SELECT * FROM consulvisor WHERE supervisor_id = #{supervisorId}) AS a " +
            "JOIN " +
            "(SELECT * FROM conversation WHERE end_time IS NOT NULL AND (#{date} = '1970-01-01' or DATE(start_time) = #{date})) AS b " +
            "ON a.consultant_id = b.to_id " +
            "JOIN " +
            "(SELECT * FROM sys_user WHERE (#{name} = '' or name LIKE CONCAT('%', #{name}, '%'))) AS c " +
            "ON b.from_id = c.id order by start_time desc ) " +
            commonSQL)
    List<Conversation> selectBoundConsultations(String name, String date, String supervisorId);

    @Select("WITH conversations AS ( " +
            "   SELECT * from conversation where from_id = #{visitorId} and end_time IS NOT NULL limit #{current}, #{size} ) " +
            commonSQL)
    List<Conversation> selectConsultationByVisitorId(String visitorId, long current, long size);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and DATE(start_time) = #{date} and is_consultation = 1;" )
    List<ConversationDO> selectConsultByDate(String date);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and DATE(start_time) = #{date} and to_id = #{toId} and is_consultation = 1" )
    List<ConversationDO> selectConsultByDateAndToId(String date, String toId);

    @Select("WITH conversations AS ( " +
            "select * from conversation where to_id = #{toId} and end_time is not NULL order by start_time desc limit 4) "
            + commonSQL)
    List<Conversation> selectRecentByToId(String toId);

    @Select("WITH conversations AS ( " +
            "SELECT * FROM conversation " +
            "where end_time IS NOT NULL and to_id = #{toId} and is_consultation = 1 limit #{current}, #{size} ) " +
            commonSQL)
    List<Conversation> selectConsultationByToId(String toId, long current, long size);

    @Select("WITH conversations AS ( " +
            "SELECT * FROM conversation where to_id = #{toId} and is_shown = 0 ) " +
            commonSQL)
    List<Conversation> selectConversationListByToId(String toId);

    @Select("WITH conversations AS ( " +
            "SELECT * FROM conversation " +
            "where end_time IS NOT NULL and to_id = #{toId} and is_consultation = 0 ) " +
            commonSQL)
    List<Conversation> selectHelpByToId(String toId);

    @Select("WITH conversations AS ( " +
            "SELECT * FROM conversation " +
            "where end_time IS NOT NULL and from_id = #{fromId} and is_consultation = 1 ) " +
            commonSQL)
    List<Conversation> selectConsultationByFromId(String fromId);


    @Select("SELECT to_id AS user_id, COUNT(*) AS total FROM conversation " +
            "WHERE MONTH(start_time) = #{month} AND end_time IS NOT NULL AND is_consultation = TRUE " +
            "GROUP BY to_id ORDER BY total DESC limit 4;")
    List<RankDO> selectMonthConsultantsInOrder(int month);

    @Select("SELECT to_id as user_id, COUNT(*) AS total FROM " +
            "(SELECT * FROM conversation WHERE MONTH(start_time) = 6 AND end_time IS NOT NULL AND is_consultation = 1) AS C " +
            " ,comment WHERE C.conversation_id = comment.conversation_id AND score = 5 AND from_id = user_id " +
            "GROUP BY to_id ORDER BY total DESC LIMIT 4;")
    List<RankDO> selectMonthGoodCommentInOrder(int month);


    @Select("SELECT * from conversation where helper_id = #{helperId}")
    ConversationDO selectByHelperId(String helperId);

    @Select("SELECT * FROM conversation where from_id = #{fromId} and to_id = #{toId} and end_time is NULL")
    ConversationDO selectByFromIdAndToId(String fromId, String toId);

    @Select("WITH conversations AS (SELECT * FROM conversation WHERE to_id = 34) " +
            commonSQL)
    List<Conversation> selectTest();
}
