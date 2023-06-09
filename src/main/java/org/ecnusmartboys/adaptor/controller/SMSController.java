package org.ecnusmartboys.adaptor.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.adaptor.Extractor;
import org.ecnusmartboys.adaptor.annotation.AnonymousAccess;
import org.ecnusmartboys.application.dto.request.command.SendSMSRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.SMSService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@Api(tags = "短信接口")
public class SMSController {
    private final SMSService smsService;

    @ApiOperation("获取短信验证码，返回后端随机生成的codeId")
    @PostMapping("/send")
    @AnonymousAccess
    public Responses<String> sendSMS(@RequestBody @Validated SendSMSRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return smsService.sendSMS(req, common);
    }
}
