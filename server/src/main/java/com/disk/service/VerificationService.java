package com.disk.service;

import com.disk.dto.VerificationDTO;

public interface VerificationService {
    public void createVerification(String email);
    public boolean verify(VerificationDTO verificationDTO);
}
