package com.mini.alladin.security;

import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.RoleRepository;
import com.mini.alladin.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * To save user + generate JWT after login
 * Called after successful Google login.
 * */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String picture = oAuth2User.getAttribute("picture");

        // Check if user exists in DB
        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        User user;

        if (existingUserOpt.isEmpty()) {
            // New user — create and save
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setProfilePicture(picture);
            user.setActive(true);
            Role defaultRole = roleRepository.findByRoleName("TRADER").orElseThrow();
            user.setRole(defaultRole);

            userRepository.save(user);
        } else {
            // Already exists
            user = existingUserOpt.get();
        }

        // Spring Security User(not entity) object for token generation
        // Creates a Spring Security User(not entity) object temporarily in memory — just to generate a JWT token.
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()))
        );

        // We generate a JWT to prove the user is logged in, and to avoid checking Google/email+password every time.
        String jwt = jwtTokenProvider.generateToken(userDetails);

        // Send token via redirect (temporary demo)
        //  redirects the user to a new URL after successful login, and attaches the JWT token in the URL as a query parameter.
        // The frontend can now read the token from the URL and store it in localStorage or cookie.
        Cookie cookie  = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        // Redirect to home page
        response.sendRedirect("/trader-dashboard");
    }
}
