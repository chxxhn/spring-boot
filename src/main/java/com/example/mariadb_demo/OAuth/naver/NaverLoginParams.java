package com.example.mariadb_demo.OAuth.naver;

import com.example.mariadb_demo.OAuth.OAuthLoginParams;
import com.example.mariadb_demo.OAuth.OAuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
public
class NaverLoginParams implements OAuthLoginParams {

    private String authorizationCode;
    private String state;

    public NaverLoginParams(String code, String state) {
        super();
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("state", state);
        return body;
    }
}
