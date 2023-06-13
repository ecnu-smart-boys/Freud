package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.data.mysql.table.MessageDO;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<MessageDO> {

    @Select("SELECT * FROM message WHERE conversation_id = #{conversationId} ORDER BY TIME DESC LIMIT #{current}, #{size}")
    List<MessageDO> selectMessageByPage(String conversationId, long current, long size);

}
