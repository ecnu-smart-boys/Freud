package org.ecnusmartboys.application.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecnusmartboys.application.dto.OnlineStaffInfo;

import java.util.List;

@ApiModel("在线信息")
@Data
@AllArgsConstructor
public class OnlineInfoResponse {

    @ApiModelProperty("在线咨询师或督导列表")
    private List<OnlineStaffInfo> staffs;

    @ApiModelProperty("在线会话数")
    private int liveConversations;

    @ApiModelProperty("总条目数")
    private int total;
}
