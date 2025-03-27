package com.example.mariadb_demo.user;

import com.example.mariadb_demo.exception.CustomExceptions;
import com.example.mariadb_demo.mail.MailService;
import com.example.mariadb_demo.sms.SmsService;
import com.example.mariadb_demo.user.login.OAuthProvider;
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
            throw new CustomExceptions.UserAlreadyExistsException("이미 등록된 이메일입니다.");
        }

        if (!mailService.isEmailVerified(userDTO.getEmail())) {
            throw new CustomExceptions.EmailNotVerifiedException("이메일 인증이 완료되지 않았습니다.");
        }
//
//        if (!smsService.isPhoneVerified(userDTO.getPhone())) {
//            throw new CustomExceptions.PhoneNotVerifiedException("전화번호 인증이 완료되지 않았습니다.");
//        }

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

    public void deleteUserById(Long id) {
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() == UserRole.ADMIN) {
            throw new IllegalStateException("관리자 계정은 삭제할 수 없습니다.");
        }
        userRepository.deleteById(id);
    }

    public void changeUserStatus(Long id, boolean status) {
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new CustomExceptions.AdminModificationNotAllowedException("관리자는 상태 변경할 수 없습니다.");
        }

        user.setEnabled(status);
        userRepository.save(user);
    }

    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

}
