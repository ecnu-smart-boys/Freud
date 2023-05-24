package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnusmartboys.infrastructure.data.mysql.VisitorInfoDO;

@Mapper
public interface VisitorInfoMapper extends BaseMapper<VisitorInfoDO> {
}
