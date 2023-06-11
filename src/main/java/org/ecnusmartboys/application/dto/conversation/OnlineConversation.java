package org.ecnusmartboys.application.dto.conversation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("展示在在线会话列表中")
public class OnlineConversation {

    @ApiModelProperty("会话id")
    private String conversationId;

    @ApiModelProperty("咨询人id")
    private String userId;

    @ApiModelProperty("咨询人头像")
    private String avatar;
}
