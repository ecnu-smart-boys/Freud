package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.data.mysql.table.UserDO;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

    @Select("SELECT * FROM sys_user WHERE role = 'supervisor' OR role = 'consultant'")
    List<UserDO> getAllStaff();
}
