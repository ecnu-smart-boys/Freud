package org.ecnusmartboys.utils;

import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ecnusmartboys.config.SmsConfig;
import org.ecnusmartboys.exception.InternalException;
import org.ecnusmartboys.model.support.SMSCode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsUtil {


    private static final String CODE_PREFIX = "smscode:";
    private static final int CODE_EXPIRE_MINUTES = 10;

    private final RedisUtil redisUtil;

    private final SmsClient smsClient;

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

    private void sendSms(String code, String phone){
        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppId(smsConfig.getSdkAppId());
        req.setSignName(smsConfig.getSignName());
        req.setTemplateId(smsConfig.getTemplateId());
        /*
         * {1}为您的登录验证码，请于{2}分钟内填写，如非本人操作，请忽略本短信。
         */
        String[] templateParamSet = {code, CODE_EXPIRE_MINUTES + ""};
        req.setTemplateParamSet(templateParamSet);
        String[] phoneNumberSet = {"+86" + phone};
        req.setPhoneNumberSet(phoneNumberSet);

        try {
            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse res = smsClient.SendSms(req);
            for (SendStatus sendStatus : res.getSendStatusSet()) {
                if (!"Ok".equals(sendStatus.getCode())) {
                    throw new InternalException("服务器短信发送异常，请稍后重试");
                }
            }
            log.info("短信发送成功，手机号：{}，返回结果：{}", phone, SendSmsResponse.toJsonString(res));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InternalException("服务器短信发送异常，请稍后重试");
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

    private static final int CODE_LENGTH = 6;

    private static final int CODE_RANGE = 1000000;

    private SMSCode generateCode() {
        String verCode = StringUtils.leftPad(String.valueOf(new Random().nextInt(CODE_RANGE)), CODE_LENGTH, "0");
        String codeId = UUID.randomUUID().toString().replaceAll("-", "");
        return new SMSCode(verCode, codeId);
    }

    private String getRedisCodeKey(String phone, String codeId) {
        return CODE_PREFIX + phone + ":" + codeId;
    }
}
