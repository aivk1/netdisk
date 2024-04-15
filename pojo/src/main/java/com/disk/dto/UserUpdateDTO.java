package com.disk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.disk.entity.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateDTO extends User{
    private String verification;

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
