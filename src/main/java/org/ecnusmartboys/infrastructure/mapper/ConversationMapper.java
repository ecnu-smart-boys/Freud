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
            "WHERE end_time IS NOT NULL and (#{date} = '1970-01-01' or DATE(start_time) = #{date}) and is_consultation = 1) AS a, " +
            "(SELECT * FROM sys_user WHERE (#{name} = '' or name LIKE CONCAT('%', #{name}, '%'))) AS b " +
            "WHERE from_id = id;")
    List<ConversationDO> selectAllConsultation(String name, String date);


    @Select("SELECT a.* FROM " +
            "(SELECT * FROM conversation " +
            "WHERE end_time IS NOT NULL and to_id = #{toId} and (#{date} = '1970-01-01' or DATE(start_time) = #{date})) AS a, " +
            "(SELECT * FROM sys_user WHERE (#{name} = '' or name LIKE CONCAT('%', #{name}, '%'))) AS b " +
            "WHERE from_id = id;")
    List<ConversationDO> selectConsultationsByToId(String name, String date, String toId);

    @Select("SELECT b.* FROM  " +
            "(SELECT * FROM consulvisor WHERE supervisor_id = #{supervisorId}) AS a " +
            "JOIN " +
            "(SELECT * FROM conversation WHERE end_time IS NOT NULL AND (#{date} = '1970-01-01' or DATE(start_time) = #{date})) AS b " +
            "ON a.consultant_id = b.to_id " +
            "JOIN " +
            "(SELECT * FROM sys_user WHERE (#{name} = '' or name LIKE CONCAT('%', #{name}, '%'))) AS c " +
            "ON b.from_id = c.id;")
    List<ConversationDO> selectBoundConsultations(String name, String date, String supervisorId);

    @Select("SELECT * from conversation where from_id = #{visitorId} and end_time IS NOT NULL")
    List<ConversationDO> selectConsultationByVisitorId(String visitorId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and DATE(start_time) = #{date} and is_consultation = 1")
    List<ConversationDO> selectConsultByDate(String date);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and DATE(start_time) = #{date} and to_id = #{toId} and is_consultation = 1")
    List<ConversationDO> selectConsultByDateAndToId(String date, String toId);

    @Select("select * from conversation where to_id = #{toId} and end_time is not NULL order by start_time desc limit 4")
    List<ConversationDO> selectRecentByToId(String toId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and to_id = #{toId} and is_consultation = 1")
    List<ConversationDO> selectConsultationByToId(String toId);

    @Select("SELECT * FROM conversation where to_id = #{toId} and is_shown = 0")
    List<ConversationDO> selectConversationListByToId(String toId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and to_id = #{toId} and is_consultation = 0")
    List<ConversationDO> selectHelpByToId(String toId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and from_id = #{fromId} and is_consultation = 1")
    List<ConversationDO> selectConsultationByFromId(String fromId);


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
}
