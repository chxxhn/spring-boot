package com.example.mariadb_demo.sms;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/signup/sms")
    public ResponseEntity<?> sendSms(@RequestBody SmsCheckDTO.SmsCertificationRequest requestDTO) {
        try {
            smsService.sendSMS(requestDTO);
            return ResponseEntity.ok().body("{\"status\": 200, \"message\": \"SMS 전송 성공\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"status\": 400, \"message\": \"SMS 전송 실패: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/signup/smsAuth")
    public ResponseEntity<Map<String, Object>> SmsVerification(@RequestBody SmsCheckDTO.SmsCertificationRequest requestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (smsService.isVerify(requestDTO)) {
                response.put("status", 200);
                response.put("message", "SMS 인증 성공");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", 400);
                response.put("message", "SMS 인증 실패");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "SMS 인증 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
