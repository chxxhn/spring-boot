package com.example.mariadb_demo.sms;

import com.example.mariadb_demo.mail.RedisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SmsService {

    private final SmsUtil smsUtil;
    private final SmsCertification smsCertification;
    private final RedisConfig redisConfig;

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
            ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
            valOperations.set("sms:verified:" + requestDTO.getPhone(), "true", 10, TimeUnit.MINUTES);
            return true;
        }

        return false;
    }

}
