package org.ecnusmartboys.application.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.MessageInfo;

import java.util.List;

@Data
@ApiModel("返回咨询消息记录+求助消息记录")
@AllArgsConstructor
@NoArgsConstructor
public class AllMsgListResponse {

    @ApiModelProperty("咨询记录列表")
    private List<MessageInfo> consultation;

    @ApiModelProperty("求助记录列表")
    private List<MessageInfo> help;

    @ApiModelProperty("是否求助了督导")
    private boolean callHelp;
}
