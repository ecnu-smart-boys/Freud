package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.application.dto.request.command.SendSMSReq;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.domain.value.SMSCode;
import org.ecnusmartboys.infrastructure.utils.CaptchaUtil;
import org.ecnusmartboys.infrastructure.utils.RedisUtil;
import org.ecnusmartboys.infrastructure.utils.RequestUtil;
import org.ecnusmartboys.infrastructure.utils.SmsUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@Api(tags = "短信接口")
public class SMSController {

    private final SmsUtil smsUtil;

    private final CaptchaUtil captchaUtil;

    private final RedisUtil redisUtil;

    @ApiOperation("获取短信验证码，返回后端随机生成的codeId")
    @PostMapping("/send")
    @AnonymousAccess
    public Response<String> sendSMS(@RequestBody @Validated SendSMSReq req, HttpServletRequest request) {
        String ip = RequestUtil.getIp(request);
        String key = "limit:sms:" + ip;
        if (redisUtil.get(key) != null) {
            throw new BadRequestException("请求过于频繁，请稍后再试");
        }

        SMSCode code = smsUtil.sendSMSCode(req.getPhone());

        redisUtil.set(key, 1, 1, TimeUnit.MINUTES);

        return Response.ok("ok", code.getCodeId());
    }
}
