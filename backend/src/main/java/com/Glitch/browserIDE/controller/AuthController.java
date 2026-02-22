package com.Glitch.browserIDE.controller;

import com.Glitch.browserIDE.dto.request.LoginRequest;
import com.Glitch.browserIDE.dto.request.RegisterRequest;
import com.Glitch.browserIDE.dto.response.AuthResponse;
import com.Glitch.browserIDE.dto.response.AuthResponseWithRefreshToken;
import com.Glitch.browserIDE.dto.response.UserDTO;
import com.Glitch.browserIDE.model.User;
import com.Glitch.browserIDE.repository.UserRepository;
import com.Glitch.browserIDE.service.AuthService;
import com.Glitch.browserIDE.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days in seconds

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthResponseWithRefreshToken result = authService.register(request);

        setRefreshTokenCookie(response, result.refreshToken);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result.authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponseWithRefreshToken result = authService.login(request);

        // Set refresh token as HttpOnly cookie
        setRefreshTokenCookie(response, result.refreshToken);

        return ResponseEntity.ok(result.authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token not found");
        }

        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(new AccessTokenResponse(newAccessToken));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken != null) {
            try {
                Long userId = jwtService.extractUserId(refreshToken);
                authService.logout(userId);
            } catch (Exception e) {
            }
        }

        clearRefreshTokenCookie(response);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        try {
            Long userId = jwtService.extractUserId(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            return ResponseEntity.ok(UserDTO.from(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private record AccessTokenResponse(String accessToken) {
    }
}
