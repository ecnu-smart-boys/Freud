package org.ecnusmartboys.application.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.MessageInfo;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("访客查看消息记录响应")
public class MsgListResponse {

    @ApiModelProperty("咨询记录列表")
    private List<MessageInfo> consultation;
}
