package org.ecnusmartboys.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("微信登录参数")
@Data
public class WxLoginReq {

    @NotNull
    String code;
}
