package com.mini.alladin.controller;


import com.mini.alladin.dto.UserCreateDTO;
import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }



    //  Create user from API (uses DTO)
    @PostMapping("/create-user")
    public UserDTO createUser(@RequestBody UserCreateDTO dto) {
        return userService.createUser(dto);
    }


    @GetMapping("/get-all")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/get-by-id/{id}")
    public UserDTO getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @GetMapping("/get-by-email/{email}")
    public UserDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }


    @PutMapping("/update/{id}")
    public UserDTO updateUser(@PathVariable int id, @RequestBody UserCreateDTO dto) {
        return userService.updateUser(id, dto);
    }


    @DeleteMapping("/delete-by-id/{id}")
    public void deleteUserById(@PathVariable int id) {
        userService.deleteUserById(id);
    }

    @DeleteMapping("/delete-by-email/{email}")
    public void deleteUserByEmail(@PathVariable String email) {
        userService.deleteUserByEmail(email);
    }



    @GetMapping("/unblock/{id}")
    public void unblockUser(@PathVariable int id, HttpServletResponse response) throws IOException {
        System.out.println("[UNBLOCK] Controller hit for user ID: " + id);

        // Clear session security context
        SecurityContextHolder.clearContext();

        // Set isActive = true in DB
        userService.unblockUserById(id);
        // Remove old JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        // Redirect to login page with success msg (in thymeleaf param.unblocked)
        response.sendRedirect("/login?unblocked=true");
    }












}
