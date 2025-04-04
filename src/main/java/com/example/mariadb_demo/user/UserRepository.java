package com.example.mariadb_demo.user;

import com.example.mariadb_demo.user.login.OAuthProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long>, JpaSpecificationExecutor<ApplicationUser> {
    Optional<ApplicationUser> findByOauthProviderAndOauthId(OAuthProvider provider, String oauthId);
    Page<ApplicationUser> findAll(Pageable pageable);
}
