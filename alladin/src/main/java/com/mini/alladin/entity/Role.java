package com.mini.alladin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    public Role(String roleName) {
        this.roleName = roleName;
    }

}
