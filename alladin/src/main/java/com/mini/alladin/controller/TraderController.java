package com.mini.alladin.controller;


import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class TraderController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfileForm(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        UserDTO user = userService.getUserByEmail(currentUser.getUsername());
        model.addAttribute("user", user);
        return "trader-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails currentUser,
                                @ModelAttribute("user") UserDTO updatedUser,
                                @RequestParam(required = false) String newPassword,
                                HttpServletResponse response) throws IOException {

        UserDTO existing = userService.getUserByEmail(currentUser.getUsername());

        updatedUser.setRole("TRADER"); // always enforce role
        userService.updateUserFromDto(existing.getUserId(), updatedUser);

        if (newPassword != null && !newPassword.isBlank()) {
            userService.setPasswordForUserById(existing.getUserId(), newPassword);
        }

        boolean emailChanged = !updatedUser.getEmail().equals(currentUser.getUsername());
        if (emailChanged) {
            SecurityContextHolder.clearContext();
            Cookie jwtCookie = new Cookie("jwt", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            response.addCookie(jwtCookie);
            response.sendRedirect("/login?logout");
            return null;
        }

        return "redirect:/trader/trader-dashboard"; // ✅ safe default
    }


}
