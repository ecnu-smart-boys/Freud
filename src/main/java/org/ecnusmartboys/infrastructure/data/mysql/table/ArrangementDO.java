package org.ecnusmartboys.infrastructure.data.mysql.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 额外排班表，多对多
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"date", "userId"}, callSuper = false)
@TableName(value = ArrangementDO.TABLE_NAME, autoResultMap = true)
public class ArrangementDO extends BaseDO {
    public static final String TABLE_NAME = "arrangement";
    private Date date;

    private Long userId;

}
