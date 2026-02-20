package com.Glitch.browserIDE.service;

import com.Glitch.browserIDE.dto.request.LoginRequest;
import com.Glitch.browserIDE.dto.request.RegisterRequest;
import com.Glitch.browserIDE.dto.response.AuthResponse;
import com.Glitch.browserIDE.dto.response.UserDTO;
import com.Glitch.browserIDE.model.User;
import com.Glitch.browserIDE.model.UserLocalAuth;
import com.Glitch.browserIDE.repository.UserLocalAuthRepository;
import com.Glitch.browserIDE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserLocalAuthRepository userLocalAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullname())
                .emailVerified(false)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        UserLocalAuth localAuth = UserLocalAuth.builder()
                .user(savedUser)
                .passwordHash(hashedPassword)
                .build();

        userLocalAuthRepository.save(localAuth);

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(UserDTO.from(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        UserLocalAuth localAuth = userLocalAuthRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), localAuth.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.getEnabled()) {
            throw new BadCredentialsException("Account is disabled");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(UserDTO.from(user))
                .build();
    }
}