package org.ecnusmartboys.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnusmartboys.infrastructure.model.mysql.Conversation;

@Mapper
public interface ConversationRepository extends BaseMapper<Conversation> {
}
