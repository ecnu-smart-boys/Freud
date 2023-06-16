package org.ecnusmartboys.application.dto.conversation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("微信访客咨询记录列表信息")
public class WxConsultRecordInfo {


    @ApiModelProperty("咨询师姓名")
    private String consultantName;

    @ApiModelProperty("咨询师头像")
    private String avatar;

    @ApiModelProperty("咨询师当前状态，是忙碌，空闲，还是未上线")
    private int state;

    @ApiModelProperty("开始时间")
    private long startTime;

    @ApiModelProperty("结束时间")
    private long endTime;

    @ApiModelProperty("我的评分")
    private int score;
}
