package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.ecnusmartboys.api.annotation.Phone;

@Data
@ApiModel("发送短信验证码请求")
public class SendSMSRequest {

    @ApiModelProperty(value = "手机号", required = true)
    @Phone
    private String phone;

}
