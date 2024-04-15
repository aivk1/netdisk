package com.disk.interceptor;

import com.disk.constant.MessageConstant;
import com.disk.constant.RedisCountConstant;
import com.disk.exception.AccessApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class RedisApiInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;
    private HashOperations<String, String, Integer> hashOperations;
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String ipAddress = request.getHeader("X-Forwarded-For");
        if(ipAddress == null){
            ipAddress = request.getRemoteAddr();
        }
        String apiAddress = request.getRequestURI();
        if(isTrafficLimited(ipAddress, apiAddress)){
            return false;
        }
        //获取redis存储内容
        return true;
    }

    public boolean isTrafficLimited(String ipAddress, String apiAddress){
        Long count = redisTemplate.opsForValue().increment("request_count:"+ipAddress+":"+apiAddress);
        if(count >= RedisCountConstant.limited) {
            log.info("IP:" + ipAddress + "Api:" + apiAddress + "已超过次数");
            return true;
        }
        return false;
    }

}
