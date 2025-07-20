package com.mini.alladin.serviceImpl;

import com.mini.alladin.dto.UserCreateDTO;
import com.mini.alladin.dto.UserDTO;
import com.mini.alladin.entity.Role;
import com.mini.alladin.entity.User;
import com.mini.alladin.repository.RoleRepository;
import com.mini.alladin.repository.UserRepository;
import com.mini.alladin.service.EmailService;
import com.mini.alladin.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final EmailService emailService;


    @Autowired
    public UserServiceImplementation(UserRepository userRepository,
                                     RoleRepository roleRepository,
                                     PasswordEncoder passwordEncoder,
                                     EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

    @Transactional
    @Override
    public void setPasswordForLoggedInUser(String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String str && !str.equals("anonymousUser")) {
            email = str;
        }

        if (email == null) {
            throw new RuntimeException("No email found in authentication");
        }

        System.out.println("✅ Logged-in email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void toggleUserActive(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean newStatus = !user.isActive();
        user.setActive(newStatus);
        userRepository.save(user);

        if (!newStatus) { // Just got blocked
            String unblockLink = "http://localhost:8080/api/users/unblock/" + user.getUserId();
            emailService.sendUnblockEmail(user.getEmail(), unblockLink);
        }
    }

    @Override
    @Transactional
    public void toggleUserRole(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        String currentRole = user.getRole().getRoleName();
        String newRole = currentRole.equals("ADMIN") ? "TRADER" : "ADMIN";

        Role role = roleRepository.findByRoleName(newRole)
                .orElseThrow(() -> new RuntimeException("Role not found: " + newRole));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserFromDto(int id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail()); // optional, if allowed to change
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void unblockUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setActive(true);
        userRepository.save(user);
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
