package org.ecnusmartboys.infrastructure.data.mysql;

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
@TableName(value = StaffInfo.TABLE_NAME, autoResultMap = true)
public class StaffInfo extends BaseModel {
    public static final String TABLE_NAME = "staff_info";
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
}
