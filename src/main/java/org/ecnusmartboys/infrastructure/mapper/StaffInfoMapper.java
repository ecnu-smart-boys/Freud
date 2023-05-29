package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.data.mysql.StaffInfoDO;

import java.util.List;

@Mapper
public interface StaffInfoMapper extends BaseMapper<StaffInfoDO> {

}
