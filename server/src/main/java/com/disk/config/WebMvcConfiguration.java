package com.disk.config;

import com.disk.interceptor.JwtTokenUserInterceptor;
import com.disk.interceptor.RedisApiInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;
    @Autowired
    private RedisApiInterceptor redisApiInterceptor;
    protected void addInterceptors(InterceptorRegistry registry){
        log.info("开始注册拦截器");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .addPathPatterns("/file/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/save");

//        registry.addInterceptor(redisApiInterceptor)
//                .addPathPatterns("/email");
    }

}
