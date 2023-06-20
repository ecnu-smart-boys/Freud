package org.ecnusmartboys.application.dto.conversation;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxConsultationInfo {

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

    @ApiModelProperty("开始时间")
    private long startTime;

    @ApiModelProperty("结束时间")
    private long endTime;
}
