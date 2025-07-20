package com.mini.alladin.controller;

import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/manage-users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-manage-users";
    }

    @PostMapping("/toggle-block/{id}")
    public String toggleBlock(@PathVariable int id, @AuthenticationPrincipal UserDetails currentUser) {
        UserDTO user = userService.getUserById(id);
        if (user.getEmail().equals(currentUser.getUsername()))
            return "redirect:/admin/manage-users?error=self-block";
        userService.toggleUserActive(id);
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/toggle-role/{id}")
    public String toggleRole(@PathVariable int id, @AuthenticationPrincipal UserDetails currentUser) {
        UserDTO user = userService.getUserById(id);
        if (user.getEmail().equals(currentUser.getUsername()))
            return "redirect:/admin/manage-users?error=self-role";
        userService.toggleUserRole(id);
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id, @AuthenticationPrincipal UserDetails currentUser) {
        UserDTO user = userService.getUserById(id);
        if (user.getEmail().equals(currentUser.getUsername()))
            return "redirect:/admin/manage-users?error=self-delete";
        userService.deleteUserById(id);
        return "redirect:/admin/manage-users";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable int id, @AuthenticationPrincipal UserDetails currentUser, Model model) {
        UserDTO user = userService.getUserById(id);
        if (!user.getEmail().equals(currentUser.getUsername()))
            return "redirect:/admin/manage-users?error=unauthorized";
        model.addAttribute("user", user);
        return "admin-update";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable int id,
                             @ModelAttribute("user") UserDTO updatedUser,
                             @RequestParam(required = false) String newPassword,
                             @AuthenticationPrincipal UserDetails currentUser) {
        UserDTO user = userService.getUserById(id);
        if (!user.getEmail().equals(currentUser.getUsername()))
            return "redirect:/admin/manage-users?error=unauthorized";

        userService.updateUserFromDto(id, updatedUser);

        if (newPassword != null && !newPassword.isBlank()) {
            userService.setPasswordForLoggedInUser(newPassword);
        }

        return "redirect:/admin/manage-users?success=updated";
    }

}
