package com.example.mariadb_demo.mail;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MailController {
    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/signup/email")
    public Map<String, String> mailSend(@RequestBody @Valid MailRequestDTO mailRequestDto) {
        String code = mailService.joinEmail(mailRequestDto.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        return response;
    }

    @PostMapping("/signup/emailAuth")
    public String authCheck(@RequestBody @Valid MailCheckDTO mailCheckDTO) {
        Boolean checked = mailService.checkAuthNum(mailCheckDTO.getEmail(), mailCheckDTO.getAuthNum());
        if (checked) {
            return "이메일 인증 성공!";
        }
        else {
            throw new NullPointerException("이메일 인증 실패!");
        }
    }


}
