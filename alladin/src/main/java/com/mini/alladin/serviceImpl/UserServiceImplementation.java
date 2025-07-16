package com.mini.alladin.serviceImpl;

import com.mini.alladin.dto.UserCreateDTO;
import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.RoleRepository;
import com.mini.alladin.repository.UserRepository;
import com.mini.alladin.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository,
                                     RoleRepository roleRepository,
                                     PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
    * return UserDTO and takes UserCreateDTO because after creating a user we don't want it to return password...
    * */
    @Override
    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {

        if(userRepository.existsByEmail(userCreateDTO.getEmail())){
            throw new RuntimeException("Email already in use....");
        }
        Role role = roleRepository.findByRoleName(userCreateDTO.getRole())
                                                    .orElseThrow(() -> new RuntimeException
                                                            ("Role not found: "+userCreateDTO.getRole()));

        User user = new User();
        user.setEmail(userCreateDTO.getEmail());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);

        return toUserDto(userRepository.save(user));
    }
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserDto).toList(); // returns list of user and we map them to dto
    }

    @Override
    public UserDTO getUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found with id: "+id));
        return toUserDto(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found with email: "+email));
        return toUserDto(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(int id, UserCreateDTO userCreateDTO) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found with id: "+id));
        if(!user.getEmail().equals(userCreateDTO.getEmail()) &&
            userRepository.existsByEmail(userCreateDTO.getEmail())){
                throw new RuntimeException("Email already in use....");
        }

        Role role = roleRepository.findByRoleName(userCreateDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found: " + userCreateDTO.getRole()));

        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setRole(role);
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());

        return toUserDto(userRepository.save(user));

    }

    @Override
    @Transactional
    public void deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteByEmail(email);
    }

    private UserDTO toUserDto(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().getRoleName());
        userDTO.setActive(user.isActive());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setCreatedAt(user.getCreatedAt());
        return userDTO;
    }

}
