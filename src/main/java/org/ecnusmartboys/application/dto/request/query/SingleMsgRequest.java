package org.ecnusmartboys.application.dto.request.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("只查询一次会话的咨询 或 求助记录封装类")
public class SingleMsgRequest {

    @NotBlank(message = "会话记录不能为空")
    private String conversationId;

    @ApiModelProperty(value = "聊天记录页码")
    @Min(value = 0, message = "聊天记录页码不合法")
    private long current = 0;

    @ApiModelProperty("聊天记录页大小")
    @Min(value = 1, message = "聊天记录页大小不合法")
    private long size;
}
