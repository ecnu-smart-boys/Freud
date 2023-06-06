package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CallHelpRequest {

    @ApiModelProperty("求助督导的id")
    @NotNull
    private String toId;

    @ApiModelProperty("被求助的会话id")
    @NotNull
    private String conversationId;

    private String myId;
}
