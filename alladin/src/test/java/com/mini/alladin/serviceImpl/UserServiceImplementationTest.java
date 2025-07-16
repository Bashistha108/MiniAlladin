package com.mini.alladin.serviceImpl;


import com.mini.alladin.dto.UserCreateDTO;
import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.RoleRepository;
import com.mini.alladin.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplementationTest {

    // @Mock to mock the repository i.e. not using the real db Objekt rather making a fake db object for test
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Inject mocks into our service class
    // create real object for somthing that we want to test
    @InjectMocks
    private UserServiceImplementation userService;

    @BeforeEach
    void setUp() {
        // Initialize all @Mock annotations before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUser_ReturnsUserDTO_WhenDataIsValid(){
        // Arrange (set up test data and mock behaviour)
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setFirstName("test");
        userCreateDTO.setLastName("test");
        userCreateDTO.setPassword("rawPassword");
        userCreateDTO.setRole("TRADER");

        Role mockRole = new Role();
        mockRole.setRoleName("TRADER");

        User savedUser  = new User();
        savedUser.setUserId(1);
        savedUser.setFirstName("test");
        savedUser.setLastName("test");
        savedUser.setPassword("rawPassword");
        savedUser.setRole(mockRole);
        savedUser.setEmail("test@example.com");

        // Tell the mock objects what to return when their methods are called
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false); // Email not used
        when(roleRepository.findByRoleName("TRADER")).thenReturn(Optional.of(mockRole)); // Role found
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword"); // Simulate password encoding
        when(userRepository.save(any(User.class))).thenReturn(savedUser); // Simulate save

        // Act (call the method to test)
        UserDTO result = userService.createUser(userCreateDTO);

        // Assert (verify output is as expected)
        Assertions.assertEquals("test@example.com", result.getEmail());
        Assertions.assertEquals("test", result.getFirstName());
        Assertions.assertEquals("TRADER", result.getRole());

        // Optionally, verify that the mocks were called
        verify(userRepository).existsByEmail("test@example.com");
        verify(roleRepository).findByRoleName("TRADER");
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository, times(2)).save(any(User.class)); // You save twice in your service


    }



}
