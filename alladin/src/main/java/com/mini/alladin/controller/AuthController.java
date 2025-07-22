package com.mini.alladin.controller;

import com.mini.alladin.dto.UserCreateDTO;
import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.RoleRepository;
import com.mini.alladin.repository.UserRepository;
import com.mini.alladin.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * This class handles actual login/register POST logic
 * - authenticates email and password during login
 * - creates and validated jwt tokens
 * - fetches roles and users from db
 * - hashes password when registering a user
 * */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Gets trigerred when user submits login form
     * Form fields are sent and spring bind them using @RequestParam
     * HttpServletResponse is used to setJWT as cookie
     * */
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (!userDetails.isEnabled()) {
                return "redirect:/login?blocked=true";
            }

            String jwt = jwtTokenProvider.generateToken(userDetails);

            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(86400);
            response.addCookie(jwtCookie);

            User user = userRepository.findByEmail(email).orElseThrow();

            if (user.getRole().getRoleId() == 2) {
                return "redirect:/trader/trader-dashboard";
            } else {
                return "redirect:/admin/admin-dashboard";
            }

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return "redirect:/login?error=true"; // show invalid credentials
        } catch (Exception e) {
            return "redirect:/login?error=true"; // fallback
        }
    }


    @PostMapping("/register")
    public String register(@ModelAttribute("userCreateDTO") UserCreateDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return "redirect:/register?error=email_exists";
        }

        Role traderRole = roleRepository.findByRoleName("TRADER")
                .orElseThrow(() -> new RuntimeException("TRADER role not found"));

        User newUser = new User();
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setRole(traderRole);
        newUser.setActive(true);

        userRepository.save(newUser);
        return "redirect:/login?registered=true";
    }


}
