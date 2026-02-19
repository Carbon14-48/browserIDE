package com.Glitch.browserIDE.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Glitch.browserIDE.model.User;
import com.Glitch.browserIDE.model.UserLocalAuth;
import com.Glitch.browserIDE.repository.UserLocalAuthRepository;
import com.Glitch.browserIDE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final UserRepository userRepository;
    private final UserLocalAuthRepository userLocalAuthRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/create-user")
    public ResponseEntity<Map<String, Object>> createTestUser() {
        User user = User.builder()
                .username("test_user_" + System.currentTimeMillis())
                .email("test" + System.currentTimeMillis() + "@example.com")
                .emailVerified(false)
                .enabled(true)
                .fullName("Test User")
                .build();
        User savedUser = userRepository.save(user);
        /// local user
        UserLocalAuth localAuth = UserLocalAuth.builder()
                .user(savedUser)
                .passwordHash("$2a$10$fake_hash_for_testing")
                .build();
        UserLocalAuth savedAuth = userLocalAuthRepository.save(localAuth);

        return ResponseEntity.ok(Map.of(
                "user", savedUser,
                "localAuth", savedAuth,
                "message", "User created successfully!"));
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/local-auth")
    public ResponseEntity<?> getLocalAuth(@PathVariable Long userId) {
        return userLocalAuthRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanup() {
        userRepository.deleteAll();
        return ResponseEntity.ok("All test data deleted!");
    }

}
