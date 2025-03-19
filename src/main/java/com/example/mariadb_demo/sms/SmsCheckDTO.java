package com.example.mariadb_demo.sms;

import lombok.Getter;

public class SmsCheckDTO {
    @Getter
    public static class SmsCertificationRequest {
        private String phone;
        private String certificationNumber;
    }
}
