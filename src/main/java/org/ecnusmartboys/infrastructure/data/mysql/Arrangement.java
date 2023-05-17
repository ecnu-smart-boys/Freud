package org.ecnusmartboys.infrastructure.data.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 排班表，多对多
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"date", "userId"}, callSuper = false)
@TableName(value = "arrangement", autoResultMap = true)
public class Arrangement extends BaseModel {

    private Date date;

    private Long userId;

}
