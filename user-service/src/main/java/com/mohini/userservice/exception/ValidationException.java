package com.mohini.userservice.exception;

/**
 * Exception thrown when business validation fails
 * Used for scenarios like: invalid input data, business rule violations
 */
public class ValidationException extends BaseException {
    
    public ValidationException(String message) {
        super(message, ErrorCodes.VALIDATION_ERROR);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, ErrorCodes.VALIDATION_ERROR, cause);
    }
    
    public static ValidationException invalidInput(String field, String value) {
        return new ValidationException("Invalid input for field '" + field + "': " + value);
    }
    
    public static ValidationException requiredField(String field) {
        return new ValidationException("Required field '" + field + "' is missing or empty");
    }
}
