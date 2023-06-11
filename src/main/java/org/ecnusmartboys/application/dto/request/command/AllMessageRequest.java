package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel("查询一次会话咨询+求助记录请求封装类")
public class AllMessageRequest {

    @NotBlank(message = "会话记录不能为空")
    private String conversationId;

    @ApiModelProperty(value = "聊天记录页码")
    @Min(value = 0, message = "聊天记录页码不合法")
    private long consultationCurrent = 0;

    @ApiModelProperty("聊天记录页大小")
    @Min(value = 1, message = "聊天记录页大小不合法")
    private long consultationSize;

    @ApiModelProperty(value = "求助记录页码")
    @Min(value = 0, message = "求助记录页码不合法")
    private long helpCurrent;

    @ApiModelProperty("求助记录页大小")
    @Min(value = 1, message = "求助记录页大小不合法")
    private long helpSize;
}
