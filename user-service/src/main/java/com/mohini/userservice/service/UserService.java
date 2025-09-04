package com.mohini.userservice.service;

import com.mohini.userservice.exception.DuplicateUserException;
import com.mohini.userservice.exception.UserNotFoundException;
import com.mohini.userservice.exception.ValidationException;
import com.mohini.userservice.model.User;
import com.mohini.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user
     * Business logic: Check for duplicate email/username
     */
    public User createUser(User user) {
        // Business validation
        if (userRepository.existsByEmail(user.getEmail())) {
            throw DuplicateUserException.withEmail(user.getEmail());
        }
        
        if (userRepository.existsByUsername(user.getUsername())) {
            throw DuplicateUserException.withUsername(user.getUsername());
        }
        
        // Set timestamps (though JPA will do this automatically)
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Save and return the user
        return userRepository.save(user);
    }

    /**
     * Find user by ID
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by email
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by username
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Update user information
     * Business logic: Check if user exists, validate email uniqueness
     */
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));
        
        // Check if email is being changed and if new email already exists
        if (!existingUser.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw DuplicateUserException.withEmail(userDetails.getEmail());
        }
        
        // Check if username is being changed and if new username already exists
        if (!existingUser.getUsername().equals(userDetails.getUsername()) && 
            userRepository.existsByUsername(userDetails.getUsername())) {
            throw DuplicateUserException.withUsername(userDetails.getUsername());
        }
        
        // Update fields
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        // Note: We don't update password here - that should be a separate method for security
        
        return userRepository.save(existingUser);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException.withId(id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Check if user exists by email
     */
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if user exists by username
     */
    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Find users by first name (case-insensitive)
     */
    public List<User> findUsersByFirstName(String firstName) {
        return userRepository.findByFirstNameIgnoreCase(firstName);
    }

    /**
     * Find users by last name (case-insensitive)
     */
    public List<User> findUsersByLastName(String lastName) {
        return userRepository.findByLastNameIgnoreCase(lastName);
    }

    /**
     * Count total users
     */
    public long getTotalUserCount() {
        return userRepository.count();
    }
}
