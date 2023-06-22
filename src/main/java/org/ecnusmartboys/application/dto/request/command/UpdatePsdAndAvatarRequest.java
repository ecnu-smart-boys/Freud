package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePsdAndAvatarRequest {

    @ApiModelProperty("用户头像")
    @NotNull
    @Range(min = 0, max = 1023, message = "头像url必须在0-1023个字符之间")
    private String avatar;

    @ApiModelProperty("用户旧密码")
    @NotNull
    @Range(min = 0, max = 255, message = "密码必须在0-1023个字符之间")
    private String oldPsd;

    @ApiModelProperty("用户旧密码")
    @NotNull
    @Range(min = 0, max = 255, message = "密码必须在0-1023个字符之间")
    private String newPsd;
}
