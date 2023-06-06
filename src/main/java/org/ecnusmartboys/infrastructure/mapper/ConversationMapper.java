package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
    List<ConversationDO> selectConsultationsByToId(String name, String date, Long toId);

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

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and to_id = #{toId} and is_consultation = 0")
    List<ConversationDO> selectHelpByToId(String toId);

    @Select("SELECT * FROM conversation " +
            "where end_time IS NOT NULL and from_id = #{fromId} and is_consultation = 1")
    List<ConversationDO> selectConsultationByFromId(String fromId);


}
