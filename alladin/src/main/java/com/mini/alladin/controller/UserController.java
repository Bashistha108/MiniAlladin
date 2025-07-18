package com.mini.alladin.controller;


import com.mini.alladin.dto.UserCreateDTO;
import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.entity.User;
import com.mini.alladin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }




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


    @PostMapping("/set-password")
    public String setPassword(@RequestParam String newPassword) {
        userService.setPasswordForLoggedInUser(newPassword);
        return "Password updated successfully";
    }

}
