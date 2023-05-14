package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("发起咨询会话请求")
public class StartConsultReq {

    @NotNull(message = "咨询师id不能为空")
    @ApiModelProperty("咨询师id")
    private Long toId;

}
