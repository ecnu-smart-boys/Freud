package org.ecnusmartboys.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.config.WeixinMpConfig;
import org.ecnusmartboys.model.response.Code2SessionResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序相关工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WxUtil {

    private final RestTemplate restTemplate;

    private final WeixinMpConfig config;

    /**
     * 通过小程序前端获取的jscode向微信请求Session相关信息
     */
    public Code2SessionResponse code2Session(String code) {
        Assert.notNull(code, "code不能为空");
        String url = "https://api.weixin.qq.com/sns/jscode2session?"
                + "appid=" + config.getAppid()
                + "&secret=" + config.getSecret()
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        return restTemplate.getForObject(url, Code2SessionResponse.class, new HashMap<>());
    }


    /**
     * 处理小程序发送的encryptedData
     *
     * @param sessionKey    从code2session中获取的sessionKey
     * @param encryptedData /
     * @param iv            /
     * @return /
     */
    public static void decryptData(String sessionKey, String encryptedData, String iv) {
        if (sessionKey == null || encryptedData == null || iv == null) {
            return;
        }
        byte[] encData = Base64Utils.decodeFromString(encryptedData);
        byte[] ivb = Base64Utils.decodeFromString(iv);
        byte[] key = Base64Utils.decodeFromString(sessionKey);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivb);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            String userInfoJson = new String(cipher.doFinal(encData), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            Map userInfo = mapper.readValue(userInfoJson, Map.class);
        } catch (Exception e) {
            log.error("error in decryptPhoneNumber: ", e);
        }
    }

}
