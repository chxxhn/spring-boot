package com.example.mariadb_demo.user;

import com.example.mariadb_demo.exception.CustomExceptions;
import com.example.mariadb_demo.user.login.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

}
