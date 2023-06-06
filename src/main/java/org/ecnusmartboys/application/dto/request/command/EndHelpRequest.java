package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EndHelpRequest {

    @NotNull(message = "求助的会话id不能为空")
    @ApiModelProperty("求助的会话id")
    private String conversationId;

    private String myId;
}
