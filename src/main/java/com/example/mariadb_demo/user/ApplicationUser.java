package com.example.mariadb_demo.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CT_USERS")
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private OAuthProvider oauthProvider;

    private boolean enabled = true;

}
