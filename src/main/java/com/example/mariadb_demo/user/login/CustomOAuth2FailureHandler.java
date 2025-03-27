package com.example.mariadb_demo.user.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        if (exception.getMessage().contains("비활성화된 계정")) {
            request.getSession().setAttribute("loginErrorMessage", "비활성화된 계정입니다.");
        } else {
            request.getSession().setAttribute("loginErrorMessage", "로그인 실패");
        }

        response.sendRedirect("/login?error");
    }
}
