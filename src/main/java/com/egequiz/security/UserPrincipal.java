package com.egequiz.security;

import com.egequiz.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security için Adapter. Domain User entity'sini, Security'nin
 * beklediği UserDetails arayüzüne dönüştürür.
 *
 * Bu sayede entity sınıfımız Spring'e bağımlı kalmaz; Security ile köprü
 * burada kurulur.
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() { return user; }

    public Long getId() { return user.getId(); }

    public String getDisplayName() { return user.getDisplayName(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security konvansiyonu: authority adı "ROLE_" ile başlamalı.
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override public String getPassword() { return user.getPassword(); }
    @Override public String getUsername() { return user.getUsername(); }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
