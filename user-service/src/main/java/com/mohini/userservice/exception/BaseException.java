package com.mohini.userservice.exception;

import java.time.LocalDateTime;

/**
 * Base exception class for all custom exceptions in the user service
 * Provides consistent error structure and timestamp
 */
public abstract class BaseException extends RuntimeException {
    
    private final String errorCode;
    private final LocalDateTime timestamp;
    
    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public BaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
