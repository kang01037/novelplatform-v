package org.example.novelplatform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniAppConfig {
    private String appId;
    private String appSecret;
}
