package com.example.mariadb_demo.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final ApplicationUser user;

    public CustomUserDetails(ApplicationUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getValue()));
    }

    @Override
    public String getPassword() { return user.getPassword(); }
    @Override
    public String getUsername() { return user.getUsername(); }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return user.isEnabled(); }
}

