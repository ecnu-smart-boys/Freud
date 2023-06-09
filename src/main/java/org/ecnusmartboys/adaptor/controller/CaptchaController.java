package org.ecnusmartboys.adaptor.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.adaptor.annotation.AnonymousAccess;
import org.ecnusmartboys.application.dto.Captcha;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.infrastructure.utils.CaptchaUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
@Api(value = "验证码API", tags = {"验证码API"})
public class CaptchaController {

    private final CaptchaUtil captchaUtil;

    @ApiOperation(value = "获取验证码",
            notes = "验证码五分钟失效，使用验证码登录无论成功与否都会失效，需要重新获取验证码")
    @GetMapping
    @AnonymousAccess
    public Responses<Captcha> getCaptcha() {
        return Responses.ok(captchaUtil.generateCaptcha());
    }
}
