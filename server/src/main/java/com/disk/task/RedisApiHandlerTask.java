package com.disk.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class RedisApiHandlerTask {

    @Autowired
    private RedisTemplate redisTemplate;
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processRedisApiCountOrder(){
        log.info("删除当天RedisApi计数器");
        Set<String> keys = redisTemplate.keys("request_count:*");
        redisTemplate.delete(keys);
    }

}
