package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("将会话从左边列表移除")
@Data
public class RemoveConversationRequest {

    @NotNull(message = "会话id不能为空")
    private String conversationId;
}
