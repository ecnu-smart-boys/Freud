package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("微信登录参数")
@Data
public class WxLoginRequest {

    @NotNull(message = "code不能为空")
    String code;
}
