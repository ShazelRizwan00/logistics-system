package com.logistics.security;

import com.logistics.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Bridges our {@link User} domain entity with Spring Security's
 * {@link UserDetails} contract.
 *
 * Spring Security calls loadUserByUsername() → returns UserPrincipal →
 * framework compares stored password hash → issues Authentication token.
 *
 * We store userId here so controllers can resolve the currently-logged-in
 * user without an extra DB round-trip.
 */
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;      // = contactInfo (email)
    private final String password;      // BCrypt hash
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.userId   = user.getUserId();
        this.username = user.getContactInfo();
        this.password = user.getPassword();
        // Spring Security requires "ROLE_" prefix for hasRole() expressions
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    // ─── Accessors ───────────────────────────────────────────────────────────

    public Long getUserId() { return userId; }

    @Override public String getUsername()  { return username; }
    @Override public String getPassword()  { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    // We are not implementing account-expiry / lock logic — always true
    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()            { return true; }
}
