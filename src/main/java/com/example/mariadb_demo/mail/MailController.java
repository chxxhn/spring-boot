package com.example.mariadb_demo.mail;

import com.example.mariadb_demo.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class MailController {

    private final MailService mailService;
    private final UserService userService;
    private final RedisConfig redisConfig;

    public MailController(MailService mailService, UserService userService, RedisConfig redisConfig) {
        this.mailService = mailService;
        this.userService = userService;
        this.redisConfig = redisConfig;
    }

    @PostMapping("/signup/email")
    public ResponseEntity<?> mailSend(@RequestBody @Valid MailRequestDTO mailRequestDto) {
        String email = mailRequestDto.getEmail();

        if (userService.isEmailDuplicated(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");
        }

        String code = mailService.joinEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/emailAuth")
    public ResponseEntity<String> authCheck(@RequestBody @Valid MailCheckDTO mailCheckDTO) {
        Boolean checked = mailService.checkAuthNum(mailCheckDTO.getEmail(), mailCheckDTO.getAuthNum());
        if (checked) {
            return ResponseEntity.ok("이메일 인증 성공!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 인증 실패!");
        }
    }

    @GetMapping("/signup/email/ttl")
    public ResponseEntity<Long> getRemainingEmailTTL(@RequestParam String email) {
        Long ttl = redisConfig.redisTemplate().getExpire(email, TimeUnit.SECONDS);

        if (ttl == null || ttl < 0) {
            return ResponseEntity.ok(0L);
        }

        return ResponseEntity.ok(ttl);
    }

}
