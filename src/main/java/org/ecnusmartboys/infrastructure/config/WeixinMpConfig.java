package org.ecnusmartboys.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "freud.weixin")
public class WeixinMpConfig {

    private String appId;

    private String secret;
}
