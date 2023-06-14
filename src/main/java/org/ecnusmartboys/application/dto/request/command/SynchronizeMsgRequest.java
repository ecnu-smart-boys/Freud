package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SynchronizeMsgRequest {

    @NotNull(message = "咨询会话id不能为空")
    @ApiModelProperty("咨询会话id")
    private String conversationId;

    @NotNull(message = "消息的迭代器不能为空")
    @ApiModelProperty("消息的迭代器，表示是否已经结束")
    @Min(value = -1, message = "-1表示获得最新的数据，其他表示上一条消息的索引")
    private int iterator;

    @Min(value = 1, message = "每次同步消息的条数必须是一个大于0的整数")
    private long size = 5L;
}
