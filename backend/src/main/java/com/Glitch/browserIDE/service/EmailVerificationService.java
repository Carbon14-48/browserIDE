package com.Glitch.browserIDE.service;

import com.Glitch.browserIDE.model.EmailVerificationToken;
import com.Glitch.browserIDE.model.User;
import com.Glitch.browserIDE.repository.EmailVerificationTokenRepository;
import com.Glitch.browserIDE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.verification.token.expiration}")
    private long tokenExpirationMs;

    @Transactional
    public void createAndSendVerificationToken(User user) {
        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(tokenExpirationMs / 1000);

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getUsername(),
                token);

        log.info("Verification token created for user: {}", user.getEmail());
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (verificationToken.isVerified()) {
            throw new IllegalArgumentException("Email already verified");
        }

        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("Verification token expired");
        }

        verificationToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("Email already verified");
        }

        createAndSendVerificationToken(user);
    }
}