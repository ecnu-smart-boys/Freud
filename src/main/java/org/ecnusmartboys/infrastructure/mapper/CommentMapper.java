package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.data.mysql.table.CommentDO;

@Mapper
public interface CommentMapper extends BaseMapper<CommentDO> {

    @Select("SELECT * FROM comment where user_id = #{userId} and conversation_id = #{conversationId} limit 1")
    CommentDO selectByUserAndConId(Long userId, Long conversationId);
}
