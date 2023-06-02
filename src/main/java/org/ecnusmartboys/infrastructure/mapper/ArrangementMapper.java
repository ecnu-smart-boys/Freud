package org.ecnusmartboys.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ecnusmartboys.infrastructure.data.mysql.intermidium.ArrangementInfo;
import org.ecnusmartboys.infrastructure.data.mysql.table.ArrangementDO;

import java.util.List;

@Mapper
public interface ArrangementMapper extends BaseMapper<ArrangementDO> {

    @Select("SELECT * FROM arrangement where date = #{date}")
    List<ArrangementDO> selectByDate(String date);

    @Select("SELECT Day(DATE) as day, role, COUNT(id) AS total FROM (SELECT * FROM arrangement WHERE YEAR(DATE) = #{year} AND MONTH(DATE) = #{month})AS \n" +
            "sub, sys_user WHERE sub.user_id = id GROUP BY DATE, role;\n")
    List<ArrangementInfo> selectInfoByYearAndMonth(Integer year, Integer month);

    @Select("SELECT Day(DATE) as day FROM arrangement WHERE YEAR(DATE) = #{year} AND MONTH(DATE) = #{month} and user_id = #{userId}")
    List<Integer> selectDayByMonthAndDateAndUserId(int year, int month, Long userId);
}
