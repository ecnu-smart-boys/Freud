package org.ecnusmartboys.infrastructure.config;


import io.github.doocs.im.ImClient;
import lombok.Data;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.MediaType;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Deflater;

@Data
@Configuration
@ConfigurationProperties(prefix = "freud.im")
public class IMConfig {

    public static final int EXPIRE = 60 * 60 * 24 * 30;
    private Long appId;
    private String secretKey;
    private String token;

    @Autowired
    private OkHttpClient httpClient;

    public String getUserSig(String userId) throws JSONException {
        return genUserSig(userId, EXPIRE);
    }

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient();
    }

    @Bean("adminClient")
    public ImClient adminClient() {
        return ImClient.getInstance(appId, "administrator", secretKey);
    }

    public String genUserSig(String userid, long expire) throws JSONException {
        return genUserSig(userid, expire, null);
    }

    public void deleteChatRecords(String fromUserId, String toUserId) {
        String baseURL = "https://console.tim.qq.com/v4/recentcontact/delete";
        String identifier = "administrator";
        String random = "99999999";

        try {
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("From_Account", fromUserId);
            requestBody.put("Type", 1);
            requestBody.put("To_Account", toUserId);
            requestBody.put("ClearRamble", 1);

            // 构建请求 URL
            String requestUrl = baseURL + "?sdkappid=" + appId + "&identifier=" + identifier +
                    "&usersig=" + getUserSig(identifier) + "&random=" + random + "&contenttype=json";

            // 创建 URL 对象
            URL url = new URL(requestUrl);

            // 创建 HttpURLConnection 对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 发送请求体
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAuthorization() {
        long currentTime = System.currentTimeMillis() / 1000;
        String sign = hmacsha256("administrator", currentTime, EXPIRE, null);
        return "TLS sig=" + sign + "&identifier=administrator&sdkappid=" + appId + "&random=" + currentTime;
    }


    private String hmacsha256(String identifier, long currTime, long expire, String base64Userbuf) {
        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
                + "TLS.sdkappid:" + appId + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";
        if (null != base64Userbuf) {
            contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
        }
        try {
            byte[] byteKey = secretKey.getBytes(StandardCharsets.UTF_8);
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            hmac.init(keySpec);
            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
            return (Base64.getEncoder().encodeToString(byteSig)).replaceAll("\\s*", "");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    private String genUserSig(String userid, long expire, byte[] userbuf) throws JSONException {

        long currTime = System.currentTimeMillis() / 1000;

        JSONObject sigDoc = new JSONObject();
        sigDoc.put("TLS.ver", "2.0");
        sigDoc.put("TLS.identifier", userid);
        sigDoc.put("TLS.sdkappid", appId);
        sigDoc.put("TLS.expire", expire);
        sigDoc.put("TLS.time", currTime);

        String base64UserBuf = null;
        if (null != userbuf) {
            base64UserBuf = Base64.getEncoder().encodeToString(userbuf).replaceAll("\\s*", "");
            sigDoc.put("TLS.userbuf", base64UserBuf);
        }
        String sig = hmacsha256(userid, currTime, expire, base64UserBuf);
        if (sig.length() == 0) {
            return "";
        }
        sigDoc.put("TLS.sig", sig);
        Deflater compressor = new Deflater();
        compressor.setInput(sigDoc.toString().getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
                0, compressedBytesLength)))).replaceAll("\\s*", "");
    }

    static class Base64URL {
        public static byte[] base64EncodeUrl(byte[] input) {
            byte[] base64 = org.apache.commons.codec.binary.Base64.encodeBase64(input);

            for (int i = 0; i < base64.length; ++i) {
                switch (base64[i]) {
                    case '+' -> base64[i] = '*';
                    case '/' -> base64[i] = '-';
                    case '=' -> base64[i] = '_';
                    default -> {
                    }
                }
            }
            return base64;
        }
    }
}
