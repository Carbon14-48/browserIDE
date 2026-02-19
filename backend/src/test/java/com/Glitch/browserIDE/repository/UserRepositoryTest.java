package com.Glitch.browserIDE.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.Glitch.browserIDE.model.User;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("User Repository Tests")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // test to save and retrieves a user
    @Test
    @DisplayName("Should save user and generate ID")
    void ShouldSaveUser() {

        // Arrange (Given)
        User user = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .emailVerified(false)
                .enabled(true)
                .fullName("John Doe")
                .build();
        // Act (when )
        User savedUser = userRepository.save(user);

        // Assert (Then)
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("john_doe");
        assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
        assertThat(savedUser.getEmailVerified()).isFalse();
        assertThat(savedUser.getEnabled()).isTrue();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    /// find user by email( user exists)
    @Test
    @DisplayName("Should find user by email when user exists")
    void shouldFindUserByEmail_WhenUserExists() {
        User user = User.builder()
                .username("jane_smith")
                .email("jane@example.com")
                .emailVerified(false)
                .enabled(true)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("jane@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("jane_smith");
        assertThat(found.get().getEmail()).isEqualTo("jane@example.com");

    }

    @Test
    @DisplayName("Should return empty Optional when email doesn't exist")
    void shouldReturnEmpty_WhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        // ASSERT - Should be empty
        assertThat(found).isEmpty();
        assertThat(found.isPresent()).isFalse();
    }

    // Find user by username
    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // ARRANGE
        User user = User.builder()
                .username("cooluser123")
                .email("cool@example.com")
                .emailVerified(true)
                .enabled(true)
                .build();
        userRepository.save(user);

        // ACT
        Optional<User> found = userRepository.findByUsername("cooluser123");

        // ASSERT
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("cool@example.com");
        assertThat(found.get().getEmailVerified()).isTrue();
    }

    // Test 5: Check if email exists (true)
    @Test
    @DisplayName("Should return true when email exists")
    void shouldReturnTrue_WhenEmailExists() {
        // ARRANGE
        User user = User.builder()
                .username("testuser")
                .email("exists@example.com")
                .emailVerified(false)
                .enabled(true)
                .build();
        userRepository.save(user);

        // ACT
        boolean exists = userRepository.existsByEmail("exists@example.com");

        // ASSERT
        assertThat(exists).isTrue();
    }

    // Test 6: Check if email exists (false)
    @Test
    @DisplayName("Should return false when email doesn't exist")
    void shouldReturnFalse_WhenEmailDoesNotExist() {
        // ACT
        boolean exists = userRepository.existsByEmail("doesnotexist@example.com");

        // ASSERT
        assertThat(exists).isFalse();
    }

    // Test 7: Check if username exists
    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckIfUsernameExists() {
        // ARRANGE
        User user = User.builder()
                .username("uniqueuser")
                .email("unique@example.com")
                .emailVerified(false)
                .enabled(true)
                .build();
        userRepository.save(user);

        // ACT
        boolean exists = userRepository.existsByUsername("uniqueuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // ASSERT
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    // Test 8: Save multiple users
    @Test
    @DisplayName("Should save multiple users")
    void shouldSaveMultipleUsers() {
        // ARRANGE
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .emailVerified(false)
                .enabled(true)
                .build();

        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .emailVerified(false)
                .enabled(true)
                .build();

        // ACT
        userRepository.save(user1);
        userRepository.save(user2);
        long count = userRepository.count();

        // ASSERT
        assertThat(count).isEqualTo(2);
    }

    // Test 9: Update user
    @Test
    @DisplayName("Should update user email")
    void shouldUpdateUserEmail() {
        User user = User.builder()
                .username("updatetest")
                .email("old@example.com")
                .emailVerified(false)
                .enabled(true)
                .build();
        User saved = userRepository.save(user);

        // ACT - update email
        saved.setEmail("new@example.com");
        User updated = userRepository.save(saved);

        // ASSERT
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getId()).isEqualTo(saved.getId()); // Same user
    }

    // Test 10: Delete user
    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // ARRANGE
        User user = User.builder()
                .username("deletetest")
                .email("delete@example.com")
                .emailVerified(false)
                .enabled(true)
                .build();
        User saved = userRepository.save(user);

        // ACT
        userRepository.deleteById(saved.getId());
        Optional<User> found = userRepository.findById(saved.getId());

        // ASSERT
        assertThat(found).isEmpty();
    }
}
