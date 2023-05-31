package org.ecnusmartboys.infrastructure.data.mysql.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 顾问与督导关系表，多对多
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"consultantId", "supervisorId"}, callSuper = false)
@TableName(value = ConsulvisorDO.TABLE_NAME, autoResultMap = true)
public class ConsulvisorDO extends BaseDO {
    public static final String TABLE_NAME = "consulvisor";
    private Long consultantId;

    private Long supervisorId;
}
