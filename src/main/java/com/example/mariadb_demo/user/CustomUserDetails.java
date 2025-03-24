package com.example.mariadb_demo.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final ApplicationUser user;
    private final Map<String, Object> attributes;

    public CustomUserDetails(ApplicationUser user) {
        this.user = user;
        this.attributes = new HashMap<>();
    }

    public CustomUserDetails(ApplicationUser user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getValue())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // 소셜 로그인은 "" 가능
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // email로도 가능
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    // OAuth2User에서 필요한 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getUsername(); // 또는 attributes.get("id").toString()
    }
}
