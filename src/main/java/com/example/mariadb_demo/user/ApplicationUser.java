package com.example.mariadb_demo.user;

import com.example.mariadb_demo.user.login.OAuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CT_USERS")
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationUser extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;

    private boolean enabled = true;

    private LocalDateTime lastLoginAt;

    public void updateLastLoginTime() {
        this.lastLoginAt = LocalDateTime.now();
    }

}
