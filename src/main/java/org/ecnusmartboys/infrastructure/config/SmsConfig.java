package org.ecnusmartboys.infrastructure.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "freud.alisms")
public class SmsConfig {

    private String accessKeyId;

    private String accessSecret;

    private String signName;

    private String templateCode;

    @Bean
    public IAcsClient createClient() {
        DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessSecret);
        return new DefaultAcsClient(profile);
    }

}
