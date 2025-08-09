package com.mini.alladin.repository;

import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * We cannot unit test interfaces directly, as they have no implementations.
 * In Spring Boot, to test a Repository we use @DataJpaTest.
 * This is an Integration Test (runs with a real or in-memory database).
 * When testing a Service that uses a Repository, we should mock the Repository.
 */

@DataJpaTest // Test Jpa Components. Configures H2 by default, EntityManager, Repository and DataSource. Rolls back transactions after each test method automatically. So no changes to db.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use the config I gave you (like H2 in application-test.properties) exactly as it is.
@ActiveProfiles("test") // Tells spring to activate test profile (use application-test.properties)
@TestPropertySource(locations = "classpath:application-test.properties") // Optional just for assurance for using application-test.properties
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    @BeforeEach
    public void createUser(){
        Role role = new Role();
        role.setRoleName("TRADER");
        roleRepository.save(role);

        User user = new User();
        user.setFirstName("test");
        user.setLastName("test");
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setRole(role);
        testUser = userRepository.save(user);

    }

    /**
     * JUnit does not support test methods with parameters by default unless using parameterized tests
     * */
    @Test
    public void testFindByEmail() {

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@test.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());

    }

    @Test
    public void testFindUserById(){
        Optional<User> foundUser = userRepository.findById(1);

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getUserId(), foundUser.get().getUserId());
    }

}

















