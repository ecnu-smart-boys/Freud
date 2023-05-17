package org.ecnusmartboys.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.model.mysql.Consulvisor;

import java.util.List;

@Mapper
public interface ConsulvisorRepository extends BaseMapper<Consulvisor> {

    @Select("SELECT * FROM consulvisor where consultant_id = #{consultantId}")
    List<Consulvisor> selectByConsultantId(@Param("consultantId") Long consultantId);

    @Select("SELECT * FROM consulvisor where supervisor_id = #{supervisorId}")
    List<Consulvisor> selectBySupervisorId(Long supervisorId);
}
