package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("后台用户登录参数")
public class StaffLoginRequest {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty("密码")
    private String password;

    @NotBlank(message = "验证码ID不能为空")
    @ApiModelProperty("验证码ID")
    private String captchaId;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty("验证码")
    private String captcha;

}
