package org.ecnusmartboys.application.dto.conversation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("显示在左下角信息")
public class LeftConversation {

    @ApiModelProperty("会话id")
    private String conversationId;

    @ApiModelProperty("咨询人id")
    private String userId;

    @ApiModelProperty("咨询人名字")
    private String name;

    @ApiModelProperty("咨询人头像")
    private String avatar;

    @ApiModelProperty("会话是否已经结束")
    private boolean isEnd;

}
