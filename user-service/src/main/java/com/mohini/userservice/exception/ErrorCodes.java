package com.mohini.userservice.exception;

/**
 * Centralized error codes for the user service
 * Eliminates hardcoded strings and provides consistent error codes
 */
public final class ErrorCodes {
    
    // Private constructor to prevent instantiation
    private ErrorCodes() {}
    
    // User-related error codes
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String DUPLICATE_USER = "DUPLICATE_USER";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    
    // Generic error codes
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final String CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";
}
