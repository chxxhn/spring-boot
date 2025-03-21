package com.example.mariadb_demo.OAuth;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    OAuthProvider getOAuthProvider();
    
    // 비즈앱 전환 후 변경
    default String getPhone() {
        return null;
    }
}
