package org.ecnusmartboys.application.dto.request.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@ApiModel("查询用户列表参数")
public class UserListReq {

    @ApiModelProperty(value = "页码")
    @Min(value = 1, message = "页码不合法")
    private int current;

    @ApiModelProperty(value = "页大小")
    @Min(value = 1, message = "页大小不合法")
    private int size;

    @ApiModelProperty("名字模糊查询，默认为空")
    private String name = "";
}
