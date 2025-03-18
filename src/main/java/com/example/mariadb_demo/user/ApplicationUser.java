package com.example.mariadb_demo.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CT_USERS")
@Data
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

    private boolean enabled = true;

    private boolean locked = false;

    private boolean verified = false;

    @Column(name = "account_credentials_expired")
    private boolean accountCredentialsExpired = false;
}
