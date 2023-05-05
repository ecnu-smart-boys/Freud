package org.ecnusmartboys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.annotation.AnonymousAccess;
import org.ecnusmartboys.exception.BadRequestException;
import org.ecnusmartboys.model.request.SendSMSReq;
import org.ecnusmartboys.model.response.BaseResponse;
import org.ecnusmartboys.model.support.SMSCode;
import org.ecnusmartboys.utils.CaptchaUtil;
import org.ecnusmartboys.utils.RedisUtil;
import org.ecnusmartboys.utils.RequestUtil;
import org.ecnusmartboys.utils.SmsUtil;
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
    public BaseResponse<String> sendSMS(@RequestBody @Validated SendSMSReq req, HttpServletRequest request) {
        String ip = RequestUtil.getIp(request);
        String key = "limit:sms:" + ip;
        if (redisUtil.get(key) != null) {
            throw new BadRequestException("请求过于频繁，请稍后再试");
        }

        SMSCode code = smsUtil.sendSMSCode(req.getPhone());

        redisUtil.set(key, 1, 1, TimeUnit.MINUTES);

        return BaseResponse.ok("ok", code.getCodeId());
    }
}
