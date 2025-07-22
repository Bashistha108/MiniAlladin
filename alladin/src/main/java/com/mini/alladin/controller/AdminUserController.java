package com.mini.alladin.controller;

import com.mini.alladin.dto.UserCreateDTO;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    // To disable block and delete buttons for self
    private boolean isSelf(int targetId, UserDetails currentUser) {
        UserDTO current = userService.getUserByEmail(currentUser.getUsername());
        return current.getUserId() == targetId;
    }

    @PostMapping("/toggle-block/{id}")
    public String toggleBlock(@PathVariable int id, @AuthenticationPrincipal UserDetails currentUser) {
        if (isSelf(id, currentUser))
            return "redirect:/admin/manage-users?error=self-block";
        userService.toggleUserActive(id);
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/toggle-role/{id}")
    public String toggleRole(@PathVariable int id, @AuthenticationPrincipal UserDetails currentUser) {
        if (isSelf(id, currentUser))
            return "redirect:/admin/manage-users?error=self-role";
        userService.toggleUserRole(id);
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id, @AuthenticationPrincipal UserDetails currentUser) {
        if (isSelf(id, currentUser))
            return "redirect:/admin/manage-users?error=self-delete";
        userService.deleteUserById(id);
        return "redirect:/admin/manage-users";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {
        UserDTO user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin-update";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable int id,
                             @ModelAttribute("user") UserDTO updatedUser,
                             @RequestParam(required = false) String newPassword,
                             @AuthenticationPrincipal UserDetails currentUser,
                             HttpServletResponse response) throws IOException {

        boolean isSelfUpdate = updatedUser.getEmail() != null &&
                currentUser.getUsername().equals(userService.getUserById(id).getEmail());

        // Update user data
        userService.updateUserFromDto(id, updatedUser);

        if (newPassword != null && !newPassword.isBlank()) {
            userService.setPasswordForUserById(id, newPassword);
        }

        // FORCE logout + JWT cleanup if it's your own account and you changed email
        if (isSelfUpdate && !updatedUser.getEmail().equals(currentUser.getUsername())) {
            SecurityContextHolder.clearContext();

            Cookie jwtCookie = new Cookie("jwt", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            response.addCookie(jwtCookie);

            response.sendRedirect("/login?logout");
            return null;
        }

        return "redirect:/admin/manage-users?success=updated";
    }


    @GetMapping("/adduser")
    public String showAddUserForm(Model model) {
        model.addAttribute("userCreateDTO", new UserCreateDTO());
        return "admin-user-add";
    }

    @PostMapping("/adduser")
    public String createUser(@ModelAttribute("userCreateDTO") UserCreateDTO dto) {
        userService.createUser(dto);
        return "redirect:/admin/manage-users?success=new-user";
    }



}
