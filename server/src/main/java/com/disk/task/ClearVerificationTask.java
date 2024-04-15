package com.disk.task;

import com.disk.mapper.VerificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClearVerificationTask {
    @Autowired
    private VerificationMapper verificationMapper;
    @Scheduled(cron = "0 0 1 * * ? ")
    public void clearVerification(){
        verificationMapper.deleteAll();
    }
}
