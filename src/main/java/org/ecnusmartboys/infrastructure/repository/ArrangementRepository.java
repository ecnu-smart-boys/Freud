package org.ecnusmartboys.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.model.mysql.Arrangement;

@Mapper
public interface ArrangementRepository extends BaseMapper<Arrangement> {

    @Select("SELECT * FROM arrangement where user_id = #{userId} limit 1")
    Arrangement selectOneArrangement(Long userId, String date);
}
