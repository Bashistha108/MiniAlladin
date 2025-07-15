package com.mini.alladin.repository;

import com.mini.alladin.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Optional is used to avoid NullPointerException. Forces us to handle something, if has null value
    // findBy + roleName attribute. Must match entity field name not db table name
    Optional<Role> findByRoleName(String roleName);
}
