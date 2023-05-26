package org.ecnusmartboys.infrastructure.config;


import com.tls.tls_sigature.tls_sigature;
import io.github.doocs.im.ImClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "freud.im")
public class IMConfig {

    private Long appId;

    private String secretKey;

    private String token;

    public static final int EXPIRE = 60 * 60 * 24 * 30;

    public String getUserSig(String userId) {
        return tls_sigature.genSig(appId, userId, EXPIRE, secretKey).urlSig;
    }

    @Bean("adminClient")
    public ImClient adminClient() {
        return ImClient.getInstance(appId, "administrator", secretKey);
    }
}
