package org.ecnusmartboys.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnusmartboys.model.entity.Rate;

@Mapper
public interface RateRepository extends BaseMapper<Rate> {
}
