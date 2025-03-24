package com.example.mariadb_demo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
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
        String provider = request.getClientRegistration().getRegistrationId(); // "kakao" or "naver"
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = extractEmail(provider, attributes);
        String name = extractName(provider, attributes);
        String phone = extractPhone(provider, attributes);

        ApplicationUser user = userRepository.findByEmail(email)
                .orElseGet(() -> registerUser(provider, email, name, phone));
        return new CustomUserDetails(user, attributes); // Spring Security 세션 저장
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
            return (String) properties.get("nickname"); // 실명 아님, 닉네임
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name"); // 네이버는 실명 가능
        }
        return null;
    }

    private String extractPhone(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("mobile"); // 네이버는 전화번호 가능
        }
        return null;
    }

    private ApplicationUser registerUser(String provider, String email, String name, String phone) {
        ApplicationUser user = ApplicationUser.builder()
                .email(email)
                .username(name != null ? name : email.split("@")[0])
                .password("") // 소셜 로그인은 비밀번호 없음
                .oauthProvider(OAuthProvider.valueOf(provider.toUpperCase()))
                .phone(phone)
                .role(UserRole.USER)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }
}

