package com.disk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableCaching
@EnableTransactionManagement
public class DiskApplication {
    public static void main(String[] args){
        SpringApplication.run(DiskApplication.class,args);
        log.info("server start");
    }
}
