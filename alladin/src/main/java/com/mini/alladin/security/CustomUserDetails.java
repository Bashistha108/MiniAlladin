package com.mini.alladin.security;

import com.mini.alladin.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
/**
 * ---------------------------------------------------------
 * CustomUserDetails
 * ---------------------------------------------------------
 * This class is a custom implementation of Spring Security's
 * UserDetails interface. It acts as an adapter that wraps your
 * application's User entity so that Spring Security can
 * understand and use it during authentication and authorization.
 *
 * Responsibilities:
 *  - Provide Spring with the user's email and password
 *  - Translate the user's role into a Spring-compatible authority (e.g., ROLE_ADMIN)
 *  - Determine if the user is active (used to block logins)
 *  - Fulfill required contract methods like account expiration, locking, etc.
 *
 * Spring uses this class:
 *  - During manual form login via AuthenticationManager
 *  - During JWT validation (to check if user is blocked)
 *
 * Notes:
 *  - The `isEnabled()` method is connected to `user.isActive()`
 *    which allows Spring to block inactive users from logging in
 *  - `getAuthorities()` returns a single role with the "ROLE_" prefix
 *
 * Connected to:
 *  - CustomUserDetailsService → returns this object when loading a user
 *  - JwtAuthenticationFilter → loads and checks this on each request
 *
 * @author Bashistha Joshi
 */

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + user.getRole().getRoleName());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
