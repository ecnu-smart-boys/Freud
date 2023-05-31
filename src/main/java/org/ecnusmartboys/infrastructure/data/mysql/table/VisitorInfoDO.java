package org.ecnusmartboys.infrastructure.data.mysql.table;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 访客信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = VisitorInfoDO.TABLE_NAME, autoResultMap = true)
public class VisitorInfoDO extends BaseDO {
    public static final String TABLE_NAME = "visitor_info";

    @ApiModelProperty("访客id")
    @TableId
    private Long visitorId;

    @ApiModelProperty(value = "紧急联系人", required = true)
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联系人电话", required = true)
    private String emergencyPhone;

}

