package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProbeRequest {

    @ApiModelProperty("正在排队的咨询师或者督导id")
    @NotNull(message = "咨询师或者督导id不能为空")
    private String consultantId;
}
