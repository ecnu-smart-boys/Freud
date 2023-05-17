package org.ecnusmartboys.infrastructure.data.mysql;


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
@TableName(value = VisitorInfo.TABLE_NAME, autoResultMap = true)
public class VisitorInfo extends BaseModel {
    public static final String TABLE_NAME = "visitor_info";

    @ApiModelProperty(value = "紧急联系人", required = true)
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联系人电话", required = true)
    private String emergencyPhone;

}

