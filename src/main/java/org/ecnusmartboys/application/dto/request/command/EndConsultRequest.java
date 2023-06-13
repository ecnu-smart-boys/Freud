package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EndConsultRequest {

    @NotNull(message = "会话id不能为空")
    @ApiModelProperty("会话id")
    private String conversationId;
}
