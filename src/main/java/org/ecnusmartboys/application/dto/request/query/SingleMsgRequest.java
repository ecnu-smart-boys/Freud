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

    @ApiModelProperty("消息迭代器")
    @NotBlank(message = "消息迭代器不能为空")
    private int iterator;

    @NotBlank(message = "获取消息的条数不能为0")
    @Min(value = 1, message = "每次获取消息的条数必须是一个正整数")
    private long size;
}
