package com.mini.alladin.controller;

import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.RoleRepository;
import com.mini.alladin.repository.UserRepository;
import com.mini.alladin.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
                return "redirect:/login?blocked=true"; // ✅ show blocked msg
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
            return "redirect:/login?error=true"; // ✅ show invalid credentials
        } catch (Exception e) {
            return "redirect:/login?error=true"; // fallback
        }
    }


    @PostMapping("/register")
    public String register(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam String password) {

        if (userRepository.existsByEmail(email)) {
            return "redirect:/register?error=email_exists";
        }

        Role traderRole = roleRepository.findByRoleName("TRADER")
                .orElseThrow(() -> new RuntimeException("TRADER role not found"));

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(traderRole);
        newUser.setActive(true);

        userRepository.save(newUser);

        return "redirect:/login?registered=true";
    }

}
