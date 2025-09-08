package com.mohini.userservice.controller;

import com.mohini.userservice.dto.CreateUserRequest;
import com.mohini.userservice.dto.UpdateUserRequest;
import com.mohini.userservice.dto.UserResponse;
import com.mohini.userservice.exception.UserNotFoundException;
import com.mohini.userservice.model.User;
import com.mohini.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*") // For development - restrict in production
@Tag(name = "User Management", description = "APIs for managing users in the system")
public class UserController {

    // Best Practice: Use static final logger with class name
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user
     * POST /api/users
     */
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user account with the provided information. Username and email must be unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                            "id": 1,
                            "username": "john_doe",
                            "email": "john@example.com",
                            "firstName": "John",
                            "lastName": "Doe",
                            "createdAt": "2024-01-15T10:30:00",
                            "updatedAt": "2024-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation errors"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username or email already exists"
        )
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
        @Parameter(description = "User creation details", required = true)
        @Valid @RequestBody CreateUserRequest request) {
        
        // Best Practice: Log incoming request (but NOT sensitive data like password)
        logger.info("Creating new user with username: {} and email: {}", 
                   request.getUsername(), request.getEmail());
        
        try {
            // Convert DTO to Entity
            User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
            );
            
            // Save user
            User savedUser = userService.createUser(user);
            
            // Best Practice: Log successful operation with user ID
            logger.info("User created successfully with ID: {} for username: {}", 
                       savedUser.getId(), savedUser.getUsername());
            
            // Convert Entity to Response DTO
            UserResponse response = convertToUserResponse(savedUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            // Best Practice: Log errors with context but not sensitive data
            logger.error("Failed to create user with username: {} and email: {}. Error: {}", 
                        request.getUsername(), request.getEmail(), e.getMessage());
            throw e; // Re-throw to let GlobalExceptionHandler handle it
        }
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id) {
        
        // Best Practice: Log the request with context
        logger.info("Retrieving user with ID: {}", id);
        
        try {
            User user = userService.findUserById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
            
            // Best Practice: Log successful retrieval
            logger.info("User retrieved successfully: ID={}, username={}", 
                       user.getId(), user.getUsername());
            
            return ResponseEntity.ok(convertToUserResponse(user));
            
        } catch (UserNotFoundException e) {
            // Best Practice: Log not found as WARN (not ERROR - it's expected sometimes)
            logger.warn("User not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            // Best Practice: Log unexpected errors
            logger.error("Unexpected error retrieving user with ID: {}. Error: {}", 
                        id, e.getMessage());
            throw e;
        }
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("Retrieving all users");
        
        try {
            List<User> users = userService.getAllUsers();
            List<UserResponse> responses = users.stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
            
            // Best Practice: Log the count of results
            logger.info("Retrieved {} users successfully", responses.size());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Failed to retrieve all users. Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Update user
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        
        // Best Practice: Log update request with context
        logger.info("Updating user with ID: {}, new username: {}, new email: {}", 
                   id, request.getUsername(), request.getEmail());
        
        try {
            // Create a temporary user with update data
            User updateData = new User();
            updateData.setUsername(request.getUsername());
            updateData.setEmail(request.getEmail());
            updateData.setFirstName(request.getFirstName());
            updateData.setLastName(request.getLastName());
            
            // Update user
            User updatedUser = userService.updateUser(id, updateData);
            
            // Best Practice: Log successful update
            logger.info("User updated successfully: ID={}, username={}", 
                       updatedUser.getId(), updatedUser.getUsername());
            
            UserResponse response = convertToUserResponse(updatedUser);
            
            return ResponseEntity.ok(response);
            
        } catch (UserNotFoundException e) {
            logger.warn("User not found for update with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Failed to update user with ID: {}. Error: {}", 
                        id, e.getMessage());
            throw e;
        }
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        try {
            userService.deleteUser(id);
            
            // Best Practice: Log successful deletion
            logger.info("User deleted successfully with ID: {}", id);
            
            return ResponseEntity.noContent().build();
            
        } catch (UserNotFoundException e) {
            logger.warn("User not found for deletion with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete user with ID: {}. Error: {}", 
                        id, e.getMessage());
            throw e;
        }
    }

    /**
     * Find user by email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        logger.info("Retrieving user by email: {}", email);
        
        try {
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
            
            logger.info("User retrieved by email successfully: ID={}, username={}", 
                       user.getId(), user.getUsername());
            
            return ResponseEntity.ok(convertToUserResponse(user));
            
        } catch (UserNotFoundException e) {
            logger.warn("User not found with email: {}", email);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving user by email: {}. Error: {}", 
                        email, e.getMessage());
            throw e;
        }
    }

    /**
     * Find user by username
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        logger.info("Retrieving user by username: {}", username);
        
        try {
            User user = userService.findUserByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
            
            logger.info("User retrieved by username successfully: ID={}, email={}", 
                       user.getId(), user.getEmail());
            
            return ResponseEntity.ok(convertToUserResponse(user));
            
        } catch (UserNotFoundException e) {
            logger.warn("User not found with username: {}", username);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving user by username: {}. Error: {}", 
                        username, e.getMessage());
            throw e;
        }
    }

    /**
     * Check if user exists by email
     * GET /api/users/exists/email/{email}
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> userExistsByEmail(@PathVariable String email) {
        logger.debug("Checking if user exists by email: {}", email);
        
        try {
            boolean exists = userService.userExistsByEmail(email);
            logger.debug("User exists by email: {} = {}", email, exists);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            logger.error("Error checking user existence by email: {}. Error: {}", 
                        email, e.getMessage());
            throw e;
        }
    }

    /**
     * Check if user exists by username
     * GET /api/users/exists/username/{username}
     */
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> userExistsByUsername(@PathVariable String username) {
        logger.debug("Checking if user exists by username: {}", username);
        
        try {
            boolean exists = userService.userExistsByUsername(username);
            logger.debug("User exists by username: {} = {}", username, exists);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            logger.error("Error checking user existence by username: {}. Error: {}", 
                        username, e.getMessage());
            throw e;
        }
    }

    /**
     * Get total user count
     * GET /api/users/count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        logger.info("Retrieving total user count");
        
        try {
            long count = userService.getTotalUserCount();
            logger.info("Total user count retrieved: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Failed to retrieve user count. Error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Helper method to convert User entity to UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
