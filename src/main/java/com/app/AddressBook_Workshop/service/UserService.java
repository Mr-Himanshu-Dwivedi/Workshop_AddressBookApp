package com.app.AddressBook_Workshop.service;

import com.app.AddressBook_Workshop.dto.UserDTO;
import com.app.AddressBook_Workshop.model.User;
import com.app.AddressBook_Workshop.repository.UserRepository;
import com.app.AddressBook_Workshop.security.JwtUtil;
import com.app.AddressBook_Workshop.security.PasswordEncoderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoderService passwordEncoderService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

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
        rabbitTemplate.convertAndSend("AddressBookExchange", "userKey", user.getEmail());
        return "User registered successfully!";
    }

    @Override
    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoderService.matches(password, userOpt.get().getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT Token
        String token = jwtUtil.generateToken(email);

        // Store token in Redis for session management (2-hour expiration)
        redisTemplate.opsForValue().set("session:" + email, token, Duration.ofHours(2));

        return token;
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

    public void logoutUser(String email) {
        redisTemplate.delete("session:" + email);
    }

    public boolean isUserLoggedIn(String email) {
        return redisTemplate.hasKey("session:" + email);
    }
}
