package org.ecnusmartboys.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("在线咨询师或督导信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineStaffInfo {

    @ApiModelProperty("咨询书或督导id")
    private String userId;

    @ApiModelProperty("咨询师或督导姓名")
    private String name;

    @ApiModelProperty("当前状态，1表示忙碌，0表示空闲")
    private int state = 0;
}
