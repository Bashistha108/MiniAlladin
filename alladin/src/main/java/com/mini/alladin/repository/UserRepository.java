package com.mini.alladin.repository;

import com.mini.alladin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Integer userId);
    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
