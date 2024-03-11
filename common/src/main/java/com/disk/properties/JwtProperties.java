package com.disk.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "disk.jwt")

public class JwtProperties {
    /**
     * admin端生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private Long adminTtl;
    private String adminTokenName;

    /**
     * 用户端生成jwt令牌相关配置
     */
    private String userSecretKey;
    private Long userTtl;
    private String userTokenName;

}
