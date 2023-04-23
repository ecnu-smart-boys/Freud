package org.ecnusmartboys.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "freud.weixin")
public class WeixinMpConfig {

    private String appid;

    private String secret;
}
