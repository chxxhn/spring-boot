package com.example.mariadb_demo.user.login;

import com.example.mariadb_demo.user.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomOAuth2FailureHandler customOAuth2FailureHandler, CustomLoginSuccessHandler customLoginSuccessHandler, CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOAuth2FailureHandler = customOAuth2FailureHandler;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationFailureHandler customAuthenticationFailureHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/admin/**", "/notice/add")).hasAuthority(UserRole.ADMIN.getValue())
                        .requestMatchers("/question/add", "/question/edit", "/question/delete", "/comment/**").authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("email")
                        .permitAll()
                        .failureHandler(customAuthenticationFailureHandler)
                        .successHandler(customLoginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/")
                        .failureHandler(customOAuth2FailureHandler)
                        .successHandler(customOAuth2SuccessHandler)
                )
                .csrf(csrf -> csrf.disable())
        ;

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
