package com.mohini.userservice.exception;

/**
 * Exception thrown when trying to create/update a user with duplicate data
 * Used for scenarios like: duplicate email, duplicate username
 */
public class DuplicateUserException extends BaseException {
    
    public DuplicateUserException(String message) {
        super(message, ErrorCodes.DUPLICATE_USER);
    }
    
    public DuplicateUserException(String message, Throwable cause) {
        super(message, ErrorCodes.DUPLICATE_USER, cause);
    }
    
    public static DuplicateUserException withEmail(String email) {
        return new DuplicateUserException("User with email '" + email + "' already exists");
    }
    
    public static DuplicateUserException withUsername(String username) {
        return new DuplicateUserException("User with username '" + username + "' already exists");
    }
}
