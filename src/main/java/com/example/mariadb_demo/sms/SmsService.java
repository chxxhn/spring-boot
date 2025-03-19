package com.example.mariadb_demo.sms;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SmsService {

    private final SmsUtil smsUtil;
    private final SmsCertification smsCertification;

    public void sendSMS(SmsCheckDTO.SmsCertificationRequest requestDTO) {
        String to = requestDTO.getPhone();

        int randomNumber = (int) (Math.random() * 9000) + 1000;
        String certificationNumber = String.valueOf(randomNumber);

        smsUtil.sendSMS(to, certificationNumber);
        smsCertification.createSmsCertification(to, certificationNumber);
    }

    public boolean isVerify(SmsCheckDTO.SmsCertificationRequest requestDTO) {
        String formCertificationNumber = requestDTO.getCertificationNumber();

        if (formCertificationNumber.equals(smsCertification.getSmsCertification(requestDTO.getPhone()))) {
            smsCertification.deleteSmsCertification(requestDTO.getPhone());
            return true;
        }

        return false;
    }
}
