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
 * ---------------------------------------------------------
 * OAuth2LoginSuccessHandler
 * ---------------------------------------------------------
 * This class defines the logic that runs immediately after a
 * successful Google OAuth2 login.
 *
 * Responsibilities:
 *  - Extract user information (email, name, picture) from the OAuth2User
 *  - Check if the user already exists in the database
 *      - If not, create a new user with TRADER role
 *  - Generate a JWT token for the authenticated user
 *  - Store the JWT token in a secure HTTP-only cookie
 *  - Redirect the user to the correct dashboard based on their role
 *
 * Why JWT is generated here:
 *  - Google login only authenticates the user once
 *  - We use JWT to keep the user logged in across requests
 *  - JWT contains email and role and is validated on every request
 *
 * Connected to:
 *  - CustomOAuth2UserService → fetches basic user info from Google
 *  - JwtTokenProvider → generates the signed JWT
 *  - UserRepository → checks or saves user data
 *  - RoleRepository → assigns default TRADER role for new users
 *
 * This class is linked in SecurityConfig under:
 *  .oauth2Login().successHandler(oAuth2LoginSuccessHandler)
 *
 * Note:
 *  - After this handler runs, Spring does NOT maintain session
 *    We must rely on the JWT for future user identification.
 *
 * @author Bashistha Joshi
 */

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
        // Because token generation expects userDetails
        // UserDetails describe the logged in user (not entity)
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), // ← the email we will put into the token
                "",              // ← password (we don’t need it for token generation)
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

        System.out.println("✅ Google login successful!");
        System.out.println("Logged in user: " + email);

        if (user.getRole().getRoleId() == 2) {
            response.sendRedirect("/trader/trader-dashboard");
        } else {
            response.sendRedirect("/admin/admin-dashboard");
        }

    }
}
