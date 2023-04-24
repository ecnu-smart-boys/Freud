package org.ecnusmartboys.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnusmartboys.model.entity.Visitor;

@Mapper
public interface VisitorRepository extends BaseMapper<Visitor> {
}
