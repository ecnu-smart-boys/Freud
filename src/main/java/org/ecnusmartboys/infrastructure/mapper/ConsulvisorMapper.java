package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.data.mysql.ConsulvisorDO;

import java.util.List;

@Mapper
public interface ConsulvisorMapper extends BaseMapper<ConsulvisorDO> {

    @Select("SELECT * FROM consulvisor where consultant_id = #{consultantId}")
    List<ConsulvisorDO> selectByConsultantId(@Param("consultantId") Long consultantId);

    @Select("SELECT * FROM consulvisor where supervisor_id = #{supervisorId}")
    List<ConsulvisorDO> selectBySupervisorId(Long supervisorId);

    @Select("DELETE FROM consulvisor where consultant_id = #{consultantId}")
    void deleteByConsultantId(@Param("consultantId") Long consultantId);
}
