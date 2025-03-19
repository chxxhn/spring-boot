package com.example.mariadb_demo.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private DefaultRedirectStrategy defaultRedirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        String username = request.getParameter("username");
        if(exception instanceof DisabledException) {
            defaultRedirectStrategy.sendRedirect(request, response, "/login-disabled");
            return;
        }
        else {
            response.sendRedirect("/login-error");
        }
    }
}

