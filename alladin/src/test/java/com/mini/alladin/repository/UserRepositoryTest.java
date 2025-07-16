package com.mini.alladin.repository;

import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    // Not mocking anything testing real behaviour if UserRepository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void UserRepository_findByEmail_ReturnsUser(){
        // Arrange
        Role role = new Role();
        role.setRoleName("ADMIN");
        roleRepository.save(role);
        User user = User.builder() // building in memory User
                .firstName("Bashistha")
                .lastName("Joshi")
                .email("bashisthajoshi108@gmail.com")
                .password("1234")
                .role(role)
                .build();


        userRepository.save(user); // save in H2
        // Act

        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        // Assert
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals(user.getEmail(), foundUser.get().getEmail());
        Assertions.assertEquals("Bashistha", foundUser.get().getFirstName());
        Assertions.assertEquals("ADMIN", foundUser.get().getRole().getRoleName());
    }

    @Test
    public void UserRepository_deleteByEmail_RemovesUser(){

        // Arrange
        Role role = new Role();
        role.setRoleName("ADMIN");
        roleRepository.save(role);

        User user = User.builder()
                .firstName("Bashisth")
                .lastName("Joshi")
                .email("bashisthajoshi108@gmail.com")
                .password("1234")
                .role(role)
                .build();
        userRepository.save(user);

        // Act
        userRepository.deleteByEmail("bashisthajoshi108@gmail.com");

        // Assert
        Assertions.assertFalse(userRepository.existsByEmail("bashisthajoshi108@gmail.com"));
        Optional<User> deletedUser = userRepository.findByEmail("bashisthajoshi108@gmail.com");
        Assertions.assertTrue(deletedUser.isEmpty());
    }

    @Test
    public void UserRepository_existsByEmail_ReturnsTrue(){
        Role role = new Role();
        role.setRoleName("ADMIN");
        roleRepository.save(role);

        User user = User.builder()
                .firstName("Bashistha")
                .lastName("Joshi")
                .email("bashistha@gmail.com")
                .password("1234")
                .role(role)
                .build();

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("bashistha@gmail.com");
        Assertions.assertTrue(exists);
    }


}
