package com.Glitch.browserIDE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Glitch.browserIDE.models.UserLocalAuth;

@Repository
public interface UserLocalAuthRepository extends JpaRepository<UserLocalAuth, Long> {

    Optional<UserLocalAuth> findByUserId(Long id);

    void deleteByUserId(Long id);
}
