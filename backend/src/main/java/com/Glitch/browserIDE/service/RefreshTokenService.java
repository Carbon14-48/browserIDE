package com.Glitch.browserIDE.service;

import com.Glitch.browserIDE.model.RefreshToken;
import com.Glitch.browserIDE.model.User;
import com.Glitch.browserIDE.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String tokenString = jwtService.generateRefreshToken(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenString)
                .user(user)
                .expiresAt(LocalDateTime.ofInstant(
                        jwtService.getRefreshTokenExpiration().toInstant(),
                        ZoneId.systemDefault()))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new IllegalArgumentException("Refresh token expired or revoked");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeUserTokens(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}