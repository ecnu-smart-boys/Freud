package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel("咨询师给会话评价的请求参数")
public class ConsultantCommentRequest {

    @NotNull(message = "会话id不能为空")
    @ApiModelProperty("会话id")
    private String conversationId;

    @NotNull(message = "评论不能为空")
    @ApiModelProperty("评论内容")
    @Size(max = 255, message = "评论不能超过255个字符")
    private String text;

    @NotNull(message = "评论标识不能为空")
    @ApiModelProperty("评论标识")
    @Size(max = 255, message = "评论标识不能超过255个字符")
    private String tag;

}
