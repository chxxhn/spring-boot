package com.example.mariadb_demo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(String username, String email, String password, boolean isAdmin) {
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(isAdmin ? UserRole.ADMIN : UserRole.USER);
        this.userRepository.save(user);
        return user;
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByname(username).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
