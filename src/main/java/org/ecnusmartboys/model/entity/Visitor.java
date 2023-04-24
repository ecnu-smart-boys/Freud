package org.ecnusmartboys.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 访客信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@TableName(value = "visitor", autoResultMap = true)
public class Visitor extends BaseEntity{

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "紧急联系人", required = true)
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联系人电话", required = true)
    private String emergencyPhone;

}

