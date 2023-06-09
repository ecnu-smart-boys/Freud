package org.ecnusmartboys.infrastructure.data.mysql.table;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 咨询师/督导信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = StaffInfoDO.TABLE_NAME, autoResultMap = true)
public class StaffInfoDO extends BaseDO {
    public static final String TABLE_NAME = "staff_info";

    @ApiModelProperty("员工id")
    @TableId
    private Long staffId;

    @ApiModelProperty("身份证号")
    private String idNumber;

    @ApiModelProperty("工作单位")
    private String department;

    @ApiModelProperty("职称")
    private String title;

    @ApiModelProperty("资质")
    private String qualification = "";

    @ApiModelProperty("资质编号")
    private String qualificationCode = "";

    @ApiModelProperty("排班计划")
    private Integer arrangement;

    @ApiModelProperty("最大同时在线会话数量")
    private Integer maxConversations;
}
