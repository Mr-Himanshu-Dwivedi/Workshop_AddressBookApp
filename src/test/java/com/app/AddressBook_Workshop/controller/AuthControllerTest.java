package com.app.AddressBook_Workshop.controller;

import com.app.AddressBook_Workshop.dto.UserDTO;
import com.app.AddressBook_Workshop.security.JwtUtil;
import com.app.AddressBook_Workshop.security.PasswordEncoderService;
import com.app.AddressBook_Workshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        UserDTO userDTO = new UserDTO("John Doe", "john@example.com", "password123");
        when(userService.registerUser(any(UserDTO.class))).thenReturn("User registered successfully");

        ResponseEntity<String> response = authController.registerUser(userDTO);

        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    void registerUser_Failure() {
        UserDTO userDTO = new UserDTO("John Doe", "john@example.com", "password123");
        when(userService.registerUser(any(UserDTO.class))).thenThrow(new RuntimeException("Email already exists"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.registerUser(userDTO);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void loginUser_Success() {
        Map<String, String> loginRequest = Map.of("email", "john@example.com", "password", "password123");
        when(userService.loginUser("john@example.com", "password123")).thenReturn("valid_jwt_token");

        ResponseEntity<Map<String, String>> response = authController.loginUser(loginRequest);

        assertEquals("valid_jwt_token", response.getBody().get("token"));
    }

    @Test
    void loginUser_Failure() {
        Map<String, String> loginRequest = Map.of("email", "john@example.com", "password", "wrongpassword");
        when(userService.loginUser("john@example.com", "wrongpassword")).thenThrow(new RuntimeException("Invalid credentials"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.loginUser(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void forgotPassword_Success() {
        when(userService.forgotPassword("john@example.com")).thenReturn("reset_token_123");

        ResponseEntity<Map<String, String>> response = authController.forgotPassword("john@example.com");

        assertEquals("reset_token_123", response.getBody().get("resetToken"));
    }

    @Test
    void forgotPassword_Failure() {
        when(userService.forgotPassword("invalid@example.com")).thenThrow(new RuntimeException("Email not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.forgotPassword("invalid@example.com");
        });

        assertEquals("Email not found", exception.getMessage());
    }

    @Test
    void resetPassword_Success() {
        Map<String, String> requestBody = Map.of(
                "email", "john@example.com",
                "token", "reset_token_123",
                "newPassword", "newPass123"
        );

        when(userService.resetPassword("john@example.com", "reset_token_123", "newPass123"))
                .thenReturn("Password reset successful");

        ResponseEntity<String> response = authController.resetPassword(requestBody);

        assertEquals("Password reset successful", response.getBody());
    }

    @Test
    void resetPassword_Failure() {
        Map<String, String> requestBody = Map.of(
                "email", "john@example.com",
                "token", "wrong_token",
                "newPassword", "newPass123"
        );

        when(userService.resetPassword("john@example.com", "wrong_token", "newPass123"))
                .thenThrow(new RuntimeException("Invalid reset token"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.resetPassword(requestBody);
        });

        assertEquals("Invalid reset token", exception.getMessage());
    }
}
