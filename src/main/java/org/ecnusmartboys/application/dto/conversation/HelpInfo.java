package org.ecnusmartboys.application.dto.conversation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("求助会话的信息")
public class HelpInfo {

    @ApiModelProperty("求助id")
    private String helpId;

    @ApiModelProperty("督导id")
    private String supervisorId;

    @ApiModelProperty("督导头像")
    private String avatar;;

    @ApiModelProperty("督导姓名")
    private String supervisorName;

    @ApiModelProperty("开始时间")
    private long startTime;

    @ApiModelProperty("最终时间")
    private long endTime;

    @ApiModelProperty("是否已经结束")
    private boolean isEnd;
}
