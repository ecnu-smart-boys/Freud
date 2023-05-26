package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.dto.SMSCode;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.SendSMSRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.SMSService;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.infrastructure.utils.RedisUtil;
import org.ecnusmartboys.infrastructure.utils.SmsUtil;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SMSServiceImpl implements SMSService {
    private final SmsUtil smsUtil;

    private final RedisUtil redisUtil;
    @Override
    public Responses<String> sendSMS(SendSMSRequest req, Common common) {
        String key = "limit:sms:" + common.getExtra().getIp();
        if (redisUtil.get(key) != null) {
            throw new BadRequestException("请求过于频繁，请稍后再试");
        }

        SMSCode code = smsUtil.sendSMSCode(req.getPhone());

        redisUtil.set(key, 1, 1, TimeUnit.MINUTES);

        return Responses.ok("ok", code.getCodeId());
    }
}
