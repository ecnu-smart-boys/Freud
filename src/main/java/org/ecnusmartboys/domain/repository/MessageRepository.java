package org.ecnusmartboys.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnusmartboys.infrastructure.model.mysql.Message;

@Mapper
public interface MessageRepository extends BaseMapper<Message> {
}
