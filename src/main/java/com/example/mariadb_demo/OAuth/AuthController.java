package com.example.mariadb_demo.OAuth;

import com.example.mariadb_demo.OAuth.kakao.KakaoLoginParams;
import com.example.mariadb_demo.OAuth.naver.NaverLoginParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final OAuthLoginService oAuthLoginService;

    public AuthController(OAuthLoginService oAuthLoginService) {
        this.oAuthLoginService = oAuthLoginService;
    }

//    @GetMapping("/kakao/callback")
//    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
//        KakaoLoginParams params = new KakaoLoginParams(code);
//        AuthTokens tokens = oAuthLoginService.login(params);
//        return ResponseEntity.ok(tokens);
//    }
//
//    @GetMapping("/naver/callback")
//    public ResponseEntity<?> naverCallback(@RequestParam String code, @RequestParam String state) {
//        NaverLoginParams params = new NaverLoginParams(code, state);
//        AuthTokens tokens = oAuthLoginService.login(params);
//        return ResponseEntity.ok(tokens);
//    }

    @PostMapping("/kakao")
    public ResponseEntity<AuthTokens> loginKakao(@RequestBody KakaoLoginParams params) {
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }

    @PostMapping("/naver")
    public ResponseEntity<AuthTokens> loginNaver(@RequestBody NaverLoginParams params) {
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }
}
