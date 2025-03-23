package com.app.AddressBook_Workshop.controller;

import com.app.AddressBook_Workshop.dto.UserDTO;
import com.app.AddressBook_Workshop.security.JwtUtil;
import com.app.AddressBook_Workshop.security.PasswordEncoderService;
import com.app.AddressBook_Workshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoderService passwordEncoderService; // Using Password Encoder Service

    // 🔹 Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.registerUser(userDTO));
    }

    // 🔹 Login User
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String token = userService.loginUser(loginRequest.get("email"), loginRequest.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    // 🔹 Forgot Password - Generate Token
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        String token = userService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("resetToken", token));
    }

    // 🔹 Reset Password Using Token
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String token = requestBody.get("token");
        String newPassword = requestBody.get("newPassword");

        String response = userService.resetPassword(email, token, newPassword);
        return ResponseEntity.ok(response);
    }

}