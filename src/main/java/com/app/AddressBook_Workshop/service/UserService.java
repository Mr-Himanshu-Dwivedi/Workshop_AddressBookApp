package com.app.AddressBook_Workshop.service;

import com.app.AddressBook_Workshop.dto.UserDTO;
import com.app.AddressBook_Workshop.model.User;
import com.app.AddressBook_Workshop.repository.UserRepository;
import com.app.AddressBook_Workshop.security.PasswordEncoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoderService passwordEncoderService;

    private final Map<String, String> resetTokens = new HashMap<>();

    @Override
    public String registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoderService.encodePassword(userDTO.getPassword()));
        user.setRole("USER");

        userRepository.save(user);
        return "User registered successfully!";
    }

    @Override
    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoderService.matches(password, userOpt.get().getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        return "Login successful";
    }

    public String forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        String resetToken = UUID.randomUUID().toString();
        resetTokens.put(email, resetToken);
        return "Use this token to reset your password: " + resetToken;
    }

    public String resetPassword(String email, String token, String newPassword) {
        if (!resetTokens.containsKey(email) || !resetTokens.get(email).equals(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
        }
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = userOpt.get();
        user.setPassword(passwordEncoderService.encodePassword(newPassword));
        userRepository.save(user);
        resetTokens.remove(email);
        return "Password reset successfully";
    }
}