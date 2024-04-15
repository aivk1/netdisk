package com.disk.service.impl;

import cn.lingyangwl.framework.tool.core.MD5Utils;
import com.disk.dto.VerificationDTO;
import com.disk.entity.Verification;
import com.disk.mapper.VerificationMapper;
import com.disk.service.VerificationService;
import com.disk.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.mail.MessagingException;
import java.time.LocalDateTime;

@Service
public class VerificationServiceImpl implements VerificationService {
    @Autowired
    private VerificationMapper verificationMapper;
    @Autowired
    private EmailUtil emailUtil;
    public void createVerification(String email) {
        String verificationValue = String.valueOf((int)((Math.random()*9+1)*100000));

        emailUtil.sendEmail(email,"验证码为:", verificationValue);
        Verification verification = Verification.builder()
                .email(email)
                .verificationValue(verificationValue)
                .createTime(LocalDateTime.now())
                .build();
        verificationMapper.insert(verification);
    }
    public boolean verify(VerificationDTO verificationDTO){
        String email = verificationDTO.getEmail();
        String verificationValue = verificationDTO.getVerification();
        Verification searchVerification = verificationMapper.getByEmail(email);
        if(searchVerification==null){
            return false;
        }
        else{
//            String MD5_verification = DigestUtils.md5DigestAsHex(searchVerificationDTO.getVerification().getBytes());
//            if(MD5_verification!=verification){
            if(!verificationValue.equals(searchVerification.getVerificationValue())){
                return false;
            }
            verificationMapper.delete(email);
            return true;
        }
    }
}
