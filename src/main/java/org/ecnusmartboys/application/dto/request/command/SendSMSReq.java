package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发送短信验证码请求")
public class SendSMSReq {

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

}
