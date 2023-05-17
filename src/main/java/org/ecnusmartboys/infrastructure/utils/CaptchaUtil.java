package org.ecnusmartboys.infrastructure.utils;

import com.wf.captcha.ArithmeticCaptcha;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.application.dto.Captcha;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaUtil {


    public static final String CAPTCHA_PREFIX = "freud_captcha:";

    public static final int CAPTCHA_EXPIRE_MINUTES = 5;

    public static final int CAPTCHA_LENGTH = 2;

    public static final int CAPTCHA_WIDTH = 120;

    public static final int CAPTCHA_HEIGHT = 32;

    private final RedisUtil redisUtil;

    public Captcha generateCaptcha() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, CAPTCHA_LENGTH);
        String verCode = captcha.text().toLowerCase();
        if (verCode.contains(".")) {
            verCode = verCode.split("\\.")[0];
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        redisUtil.set(CAPTCHA_PREFIX + uuid, verCode, CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return new Captcha(verCode, captcha.toBase64(), uuid);
    }

    public boolean verifyCaptcha(String codeUuid, String code) throws BadRequestException {
        Assert.notNull(codeUuid, "验证码ID不能为空");
        Assert.notNull(code, "验证码不能为空");

        String actualCode = (String) redisUtil.get(CAPTCHA_PREFIX + codeUuid);
        redisUtil.del(CAPTCHA_PREFIX + codeUuid);
        if (Strings.isBlank(actualCode)) {
            return false;
        }
        return !Strings.isBlank(code) && code.equalsIgnoreCase(actualCode);
    }

    public void invalidateCaptcha(String codeUuid) {
        Assert.isTrue(Strings.isNotBlank(codeUuid), "验证码ID不能为空");

        redisUtil.del(CAPTCHA_PREFIX + codeUuid);
    }
}
