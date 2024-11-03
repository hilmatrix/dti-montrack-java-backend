package com.hilmatrix.montrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.hilmatrix.montrack.request.SignupRequest;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@RequestMapping("/api/v1")
public class SignupController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        // Check if password and password confirmation match
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirmation())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match.");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // Insert new user into the database
        String sql = "INSERT INTO users (email, password_hash, name, created_at, updated_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        jdbcTemplate.update(sql, signupRequest.getEmail(), hashedPassword, signupRequest.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }
}
