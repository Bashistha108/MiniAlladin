package com.mini.alladin.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * This DTO is response(what we send back to client)
 * We use String for Role because we only want to pass the role_name
 * Output shown to client
 * From API -> Client
 * */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String profilePicture;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
