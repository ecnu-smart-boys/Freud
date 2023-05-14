package org.ecnusmartboys.infrastructure.model.mysql;

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
@TableName(value = "staff", autoResultMap = true)
public class Staff extends BaseModel {

    @ApiModelProperty("身份证号")
    private String idNumber;

    @ApiModelProperty("工作单位")
    private String department;

    @ApiModelProperty("职称")
    private String title;

    @ApiModelProperty("平台督导的时间累计")
    private Long totalTime;

    @ApiModelProperty("资质")
    private String qualification;

    @ApiModelProperty("资质编号")
    private String qualificationCode;

    @ApiModelProperty("一周排班, 1<<(1-7)分别代表周一到周日, 0代表休息, 1代表上班")
    private byte arrangement;
}
