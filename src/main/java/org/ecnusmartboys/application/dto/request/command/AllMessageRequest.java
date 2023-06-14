package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("查询一次会话咨询+求助记录请求封装类")
public class AllMessageRequest {

    @NotBlank(message = "会话id不能为空")
    private String conversationId;

    @NotBlank(message = "咨询会话消息的迭代器不能为空")
    @ApiModelProperty("咨询会话消息的迭代器，-1表示首页，0表示不用传")
    private int consultationIterator;

    @NotBlank(message = "求助消息的迭代器不能为空")
    @ApiModelProperty("求助消息的迭代器，-1表示首页，0表示不用传")
    private int helpIterator;

    @NotNull(message = "分页大小不能为空")
    @ApiModelProperty
    private long size;

//    @ApiModelProperty(value = "聊天记录页码")
//    @Min(value = 0, message = "聊天记录页码不合法")
//    private long consultationCurrent = 0;
//
//    @ApiModelProperty("聊天记录页大小")
//    @Min(value = 1, message = "聊天记录页大小不合法")
//    private long consultationSize;
//
//    @ApiModelProperty(value = "求助记录页码")
//    @Min(value = 0, message = "求助记录页码不合法")
//    private long helpCurrent;
//
//    @ApiModelProperty("求助记录页大小")
//    @Min(value = 1, message = "求助记录页大小不合法")
//    private long helpSize;
}
