package com.mini.alladin.dto;

import lombok.*;

/**
 * This DTO is Request(what we receive from client)
 * From client -> API
 * Input to create user
 * */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String profilePicture;
}
