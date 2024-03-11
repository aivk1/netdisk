package com.disk.config;

import com.disk.properties.AliOSSProperties;
import com.disk.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OSSConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOSSProperties aliOSSProperties){
        log.info("开始创建阿里云文件上传工具对象:{}", aliOSSProperties);
        return new AliOssUtil(aliOSSProperties.getEndpoint(),
                aliOSSProperties.getAccessKeyId(),
                aliOSSProperties.getAccessKeySecret(),
                aliOSSProperties.getBucketName());
    }
}
