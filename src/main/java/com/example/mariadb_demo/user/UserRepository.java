package com.example.mariadb_demo.user;

import com.example.mariadb_demo.user.login.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByOauthProviderAndOauthId(OAuthProvider provider, String oauthId);
}
