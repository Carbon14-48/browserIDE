package com.Glitch.browserIDE.repository;

import com.Glitch.browserIDE.model.EmailVerificationToken;
import com.Glitch.browserIDE.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUserAndVerifiedAtIsNull(User user);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    void deleteByUser(User user);
}