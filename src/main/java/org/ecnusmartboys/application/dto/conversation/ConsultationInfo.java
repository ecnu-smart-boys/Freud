package org.ecnusmartboys.application.dto.conversation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("咨询会话的信息")
public class ConsultationInfo {

    @ApiModelProperty("会话id")
    private String consultationId;

    @ApiModelProperty("咨询师id")
    private String consultantId;

    @ApiModelProperty("访客id")
    private String visitorId;

    @ApiModelProperty("咨询师姓名")
    private String consultantName;

    @ApiModelProperty("咨询师头像")
    private String consultantAvatar;

    @ApiModelProperty("访客电话")
    private String phone;

    @ApiModelProperty("访客姓名")
    private String visitorName;

    @ApiModelProperty("访客头像")
    private String visitorAvatar;

    @ApiModelProperty("开始时间")
    private long startTime;

    @ApiModelProperty("持续时间")
    private long lastTime;

    @ApiModelProperty("会话是否已结束")
    private boolean isEnd;

}
