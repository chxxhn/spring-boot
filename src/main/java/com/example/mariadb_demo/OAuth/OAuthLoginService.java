package com.example.mariadb_demo.OAuth;

import com.example.mariadb_demo.user.ApplicationUser;
import com.example.mariadb_demo.user.UserRepository;
import com.example.mariadb_demo.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public AuthTokens login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long memberId = findOrCreateMember(oAuthInfoResponse);
        return authTokensGenerator.generate(memberId);
    }

    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return userRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(ApplicationUser::getId)
                .orElseGet(() -> newMember(oAuthInfoResponse));
    }

    private Long newMember(OAuthInfoResponse oAuthInfoResponse) {
        String username = (oAuthInfoResponse.getNickname() != null && !oAuthInfoResponse.getNickname().isEmpty())
                ? oAuthInfoResponse.getNickname() : oAuthInfoResponse.getEmail();

        ApplicationUser user = ApplicationUser.builder()
                .username(username)
                .password("")
                .email(oAuthInfoResponse.getEmail())
                .phone(oAuthInfoResponse.getPhone())
                .role(UserRole.USER)
                .oauthProvider(oAuthInfoResponse.getOAuthProvider())
                .enabled(true)
                .build();

        return userRepository.save(user).getId();
    }
}