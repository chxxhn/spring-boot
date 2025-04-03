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
        OAuthProvider oauthProviderEnum = OAuthProvider.valueOf(provider.toUpperCase());
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauthId = extractOAuthId(provider, attributes);
        String email = extractEmail(provider, attributes);
        String name = extractName(provider, attributes);
        String phone = extractPhone(provider, attributes);
        String gender = extractGender(provider, attributes);
        String birthday = extractBirthday(provider, attributes);
        String birthYear = extractBirthYear(provider, attributes);

        ApplicationUser user = userRepository.findByOauthProviderAndOauthId(oauthProviderEnum, oauthId)
                .orElseGet(() -> registerUser(oauthProviderEnum, oauthId, email, name, phone, gender, birthday, birthYear));

        if (!user.isEnabled()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("disabled"), "비활성화된 계정입니다.");
        }

        return new CustomUserDetails(user, attributes);
    }

    private String extractOAuthId(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            return String.valueOf(attributes.get("id"));
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("id");
        }
        return null;
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
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("name");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name");
        }
        return null;
    }

    private String extractPhone(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("phone_number");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("mobile");
        }
        return null;
    }

    private String extractGender(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("gender");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("gender");
        }
        return null;
    }

    private String extractBirthday(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("birthday");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("birthday");
        }
        return null;
    }

    private String extractBirthYear(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("birthyear");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("birthyear");
        }
        return null;
    }

    private ApplicationUser registerUser(OAuthProvider provider, String oauthId, String email, String name,
                                         String phone, String gender, String birthday, String birthYear) {

        ApplicationUser user = ApplicationUser.builder()
                .oauthProvider(provider)
                .oauthId(oauthId)
                .email(email)
                .username(name)
                .phone(phone)
                .gender(gender)
                .birthday(birthday)
                .birthYear(birthYear)
                .role(UserRole.USER)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }
}
