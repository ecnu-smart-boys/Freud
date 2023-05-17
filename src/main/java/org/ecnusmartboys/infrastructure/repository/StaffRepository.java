package org.ecnusmartboys.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnusmartboys.infrastructure.data.mysql.Staff;

@Mapper
public interface StaffRepository extends BaseMapper<Staff> {
}
