package com.example.mariadb_demo.user;

import com.example.mariadb_demo.mail.MailService;
import com.example.mariadb_demo.sms.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SmsService smsService;

    public ApplicationUser createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }
        /*if (!mailService.isEmailVerified(userDTO.getEmail())) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }
        if (!smsService.isPhoneVerified(userDTO.getPhone())) {
            throw new IllegalArgumentException("전화번호 인증이 완료되지 않았습니다.");
        }*/
        ApplicationUser user = new ApplicationUser();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword1()));
        user.setPhone(userDTO.getPhone());
        user.setRole(UserRole.USER);
        user.setOauthProvider(OAuthProvider.LOCAL);
        this.userRepository.save(user);
        return user;
    }

    public List<ApplicationUser> getAllUsers() {
        return userRepository.findAll();
    }

}
