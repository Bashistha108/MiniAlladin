package com.mini.alladin.security;

import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.RoleRepository;
import com.mini.alladin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * ---------------------------------------------------------
 * CustomOAuth2UserService
 * ---------------------------------------------------------
 * This service is automatically triggered by Spring Security
 * after a successful Google OAuth2 login.
 *
 * Responsibilities:
 *  - Fetch user profile info (email, first name, last name) from Google
 *  - Check if the user already exists in the database
 *      - If user does NOT exist, create a new one with the TRADER role
 *      - If user already exists, return the existing user
 *  - This service handles only data-related logic (user creation)
 *    and does NOT generate JWT or redirect (that’s the handler's job)
 *
 * Connected to:
 *  - Used in SecurityConfig under:
 *    .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService)
 *  - OAuth2LoginSuccessHandler receives the returned OAuth2User
 *    and handles authentication/token logic
 *
 * Design Note:
 *  - This class keeps DB creation logic separate from authentication logic
 *  - Makes the code easier to maintain and test
 *
 * This does NOT handle token generation or cookie setting.
 *     That is handled in OAuth2LoginSuccessHandler.
 *
 * @author Bashistha Joshi
 */

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        // Fetch user from google
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extract email and name from Google's user info
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        // Check if user already exists in DB by email
        userRepository.findByEmail(email).orElseGet(()->{
            // If user not found, create a new user
            Role traderRole = roleRepository.findByRoleName("TRADER").orElseThrow(()->new RuntimeException("Error, role not found"));
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPassword(""); // Not used for Google login
            newUser.setRole(traderRole);

            return userRepository.save(newUser);

        });

        return oAuth2User;
    }

}
