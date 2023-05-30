package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.domain.model.arrangement.ArrangementInfo;
import org.ecnusmartboys.infrastructure.data.mysql.ArrangementDO;

import java.util.List;

@Mapper
public interface ArrangementMapper extends BaseMapper<ArrangementDO> {

    @Select("SELECT * FROM arrangement where date = #{date}")
    List<ArrangementDO> selectByDate(String date);

    @Select("SELECT Day(DATE) as day, role, COUNT(id) AS total FROM (SELECT * FROM arrangement WHERE YEAR(DATE) = #{year} AND MONTH(DATE) = #{month})AS \n" +
            "sub, sys_user WHERE sub.user_id = id GROUP BY DATE, role;\n")
    List<ArrangementInfo> selectInfoByMonthAndDate(Integer year, Integer month);
}
