package org.ecnusmartboys.application.dto.request.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

@ApiModel("查询当前在线员工")
@Data
public class OnlineStaffListRequest {

    @ApiModelProperty("页码")
    @Min(value = 0, message = "页码不能为负数")
    private long current = 0;

    @ApiModelProperty("页码大小")
    @Min(value = 0, message = "页码大小必须是一个大于0的整数")
    private long size = 5L;
}
