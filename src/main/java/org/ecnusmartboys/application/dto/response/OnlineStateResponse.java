package org.ecnusmartboys.application.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.conversation.LeftConversation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineStateResponse {

    @ApiModelProperty("0表示没有会话，1表示有排队会话，2表示有在线会话")
    private int state;

    private LeftConversation conversation;

    @ApiModelProperty("开始时间")
    private long startTime;
}
