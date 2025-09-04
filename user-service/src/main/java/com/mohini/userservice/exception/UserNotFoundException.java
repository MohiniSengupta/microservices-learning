package com.mohini.userservice.exception;

/**
 * Exception thrown when a user is not found
 * Used for scenarios like: find by ID, email, or username
 */
public class UserNotFoundException extends BaseException {
    
    public UserNotFoundException(String message) {
        super(message, ErrorCodes.USER_NOT_FOUND);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, ErrorCodes.USER_NOT_FOUND, cause);
    }
    
    public static UserNotFoundException withId(Long id) {
        return new UserNotFoundException("User not found with id: " + id);
    }
    
    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }
    
    public static UserNotFoundException withUsername(String username) {
        return new UserNotFoundException("User not found with username: " + username);
    }
}
