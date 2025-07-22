package com.mini.alladin.security;

import com.mini.alladin.entity.User;
import com.mini.alladin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ---------------------------------------------------------
 * CustomUserDetailsService
 * ---------------------------------------------------------
 * This service is used by Spring Security to load user details
 * during both form login and JWT-based authentication.
 *
 *  Responsibilities:
 *  - Load a user from the database using their email (as username)
 *  - If user is found, wrap it inside a CustomUserDetails object
 *  - If not found, throw a UsernameNotFoundException
 *
 *  Spring automatically uses this class when:
 *  - A user logs in via form (`/auth/login`)
 *  - A JWT token is being validated in JwtAuthenticationFilter
 *
 *  This is required because Spring Security needs a consistent way
 *     to retrieve user info like email, password, roles, and status.
 *
 *  Connected to:
 *  - UserRepository → to fetch user by email
 *  - CustomUserDetails → wraps your User entity to match Spring format
 *
 *  IMPORTANT:
 *  - This class does NOT perform password checks
 *  - It only provides the user data — Spring checks the password
 *
 * @author Bashistha Joshi
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new CustomUserDetails(user); // ✅ Full user, with isActive logic
    }




}
