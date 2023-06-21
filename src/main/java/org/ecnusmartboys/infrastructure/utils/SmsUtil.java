package org.ecnusmartboys.infrastructure.utils;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ecnusmartboys.application.dto.SMSCode;
import org.ecnusmartboys.infrastructure.config.SmsConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsUtil {


    private static final String CODE_PREFIX = "smscode:";
    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final int CODE_LENGTH = 6;
    private static final int CODE_RANGE = 1000000;
    private final RedisUtil redisUtil;
    private final SmsConfig smsConfig;

    public SMSCode sendSMSCode(String phone) {
        Assert.notNull(phone, "手机号不能为空");

        if (EnvironmentUtil.contains("dev") || EnvironmentUtil.contains("test")) {
            String verCode = "102030";
            String codeId = UUID.randomUUID().toString().replaceAll("-", "");
            redisUtil.set(getRedisCodeKey(phone, codeId), verCode, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            return new SMSCode(verCode, codeId);
        }

        SMSCode code = generateCode();

        sendSms(code.getCode(), phone);

        redisUtil.set(getRedisCodeKey(phone, code.getCodeId()), code.getCode(), CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return code;
    }

    private void sendSms(String code, String phone) {

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        // 阿里云服务器域名
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        // 短信接收者手机号
        request.putQueryParameter("PhoneNumbers", phone);
        // 短信签名
        request.putQueryParameter("SignName", smsConfig.getSignName());
        // 模板ID
        request.putQueryParameter("TemplateCode", smsConfig.getTemplateCode());

        Map<String, String> jsonParam = new HashMap<>();
        jsonParam.put("code", code);

        request.putQueryParameter("TemplateParam", JSON.toJSONString(jsonParam));

        CommonResponse response = null;
        try {
            response = smsConfig.createClient().getCommonResponse(request);
            if(response.getHttpResponse().isSuccess()) {
                log.info("短信发送成功，手机号：{}，返回结果：{}", phone, response.getData());
            }
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean verifyCode(String phone, String codeId, String code) {
        if (phone == null || codeId == null || code == null) {
            return false;
        }
        String correctCode = (String) redisUtil.get(getRedisCodeKey(phone, codeId));
        boolean res = code.equals(correctCode);
        if (res) {
            invalidateCode(phone, codeId);
        }
        return res;
    }

    public void invalidateCode(String phone, String codeId) {
        redisUtil.del(getRedisCodeKey(phone, codeId));
    }

    private SMSCode generateCode() {
        String verCode = StringUtils.leftPad(String.valueOf(new Random().nextInt(CODE_RANGE)), CODE_LENGTH, "0");
        String codeId = UUID.randomUUID().toString().replaceAll("-", "");
        return new SMSCode(verCode, codeId);
    }

    private String getRedisCodeKey(String phone, String codeId) {
        return CODE_PREFIX + phone + ":" + codeId;
    }
}
