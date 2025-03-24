package com.example.mariadb_demo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
