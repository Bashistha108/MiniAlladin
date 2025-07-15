package com.mini.alladin.service;

import com.mini.alladin.dto.UserCreateDTO;
import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {

    UserDTO createUser(UserCreateDTO userCreateDTO);

    List<UserDTO> getAllUsers();
    UserDTO getUserById(int id);
    UserDTO getUserByEmail(String email);

    UserDTO updateUser(int id, UserCreateDTO userCreateDTO);

    void deleteUserById(int id);
    void deleteUserByEmail(String email);

}
