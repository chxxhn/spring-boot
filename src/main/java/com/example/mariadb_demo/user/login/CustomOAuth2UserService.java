package com.example.mariadb_demo.user.login;

import com.example.mariadb_demo.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        String provider = request.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = extractEmail(provider, attributes);
        String name = extractName(provider, attributes);
        String phone = extractPhone(provider, attributes);

        ApplicationUser user = userRepository.findByEmail(email)
                .orElseGet(() -> registerUser(provider, email, name, phone));

        if (!user.isEnabled()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("disabled"), "비활성화된 계정입니다.");
        }

        return new CustomUserDetails(user, attributes);
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("email");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }
        return null;
    }

    private String extractName(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return (String) properties.get("nickname"); // 비즈 앱 전환후 변경
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name"); 
        }
        return null;
    }

    private String extractPhone(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("mobile"); 
        }
        return null;
    }

    private ApplicationUser registerUser(String provider, String email, String name, String phone) {
        ApplicationUser user = ApplicationUser.builder()
                .email(email)
                .username(name != null ? name : email.split("@")[0])
                .password("") 
                .oauthProvider(OAuthProvider.valueOf(provider.toUpperCase()))
                .phone(phone)
                .role(UserRole.USER)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }
}

