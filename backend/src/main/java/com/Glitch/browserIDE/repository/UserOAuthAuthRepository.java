package com.Glitch.browserIDE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Glitch.browserIDE.models.UserOAuthAuth;
import com.Glitch.browserIDE.models.AuthProvider;

@Repository
public interface UserOAuthAuthRepository extends JpaRepository<UserOAuthAuth, Long> {

    Optional<UserOAuthAuth> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    List<UserOAuthAuth> findByUserId(Long userId);

    boolean existsByUserIdAndProvider(Long userId, AuthProvider provider);

    void deleteByUserIdAndProvider(Long userId, AuthProvider provider);
}
