package com.example.mariadb_demo.mail;

import com.example.mariadb_demo.exception.CustomExceptions;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisConfig redisConfig;

    public MailService(JavaMailSender javaMailSender, RedisConfig redisConfig) {
        this.javaMailSender = javaMailSender;
        this.redisConfig = redisConfig;
    }

    @Value("${spring.mail.username}")
    private String serviceName;

    private int generateRandomNumber() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }
        return Integer.parseInt(randomNumber.toString());
    }

    public void mailSend(String setFrom, String toMail, String title, String content, int authNumber) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        // Redis에 30초 동안 이메일과 인증 코드를 저장
        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        valOperations.set(toMail, Integer.toString(authNumber), 30, TimeUnit.SECONDS);
    }

    public String joinEmail(String email) {
        int authNumber = generateRandomNumber();
        String title = "회원 가입을 위한 이메일입니다!";
        String content = "이메일을 인증하기 위한 절차입니다." +
                "<br><br>" +
                "인증 번호는 " + authNumber + "입니다." +
                "<br>" +
                "회원 가입 폼에 해당 번호를 입력해주세요.";
        mailSend(serviceName, email, title, content, authNumber);
        return Integer.toString(authNumber);
    }

    public Boolean checkAuthNum(String email, String authNum) {
        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        String code = valOperations.get(email);
        if (code == null) {
            throw new CustomExceptions.EmailExpiredException("인증 시간이 초과되었습니다.");
        }

        if (!Objects.equals(code, authNum)) {
            throw new CustomExceptions.EmailCodeMismatchException("인증번호가 일치하지 않습니다.");
        }

        valOperations.set("email:verified:" + email, "true", 10, TimeUnit.MINUTES);
        return true;
    }

    public boolean isEmailVerified(String email) {
        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        return "true".equals(valOperations.get("email:verified:" + email));
    }
}
