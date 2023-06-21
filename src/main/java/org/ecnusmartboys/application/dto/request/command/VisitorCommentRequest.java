package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class VisitorCommentRequest {

    @NotNull(message = "会话id不能为空")
    @ApiModelProperty("会话id")
    private String conversationId;

    @NotNull(message = "评论不能为空")
    @ApiModelProperty("评论")
    @Size(max = 255, message = "评论不能超过255个字符")
    private String text;

    @NotNull(message = "评分不能为空")
    @ApiModelProperty("评分")
    @Range(min = 1, max = 5, message = ("评分必须是1 - 5之间的一个整数"))
    private int score;
}
