package com.app.ecommerce.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    VALIDATION_ERROR(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Validation failed"
    ),
    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "ACCESS_DENIED",
            "You do not have permission to access this resource"
    ),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED,
            "BAD_CREDENTIALS",
            "Invalid username or password"
    ),
    RESOURCE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND",
            "Requested resource was not found"
    ),
    RESOURCE_CONFLICT(
            HttpStatus.CONFLICT,
            "RESOURCE_CONFLICT",
            "Resource already exists or conflicts with current state"
    ),
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred"
    ),
    ACCOUNT_DISABLED(
            HttpStatus.UNAUTHORIZED,
            "ACCOUNT_DISABLED",
            "Your account has been disabled"
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}