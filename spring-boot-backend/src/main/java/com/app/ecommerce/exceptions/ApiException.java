package com.app.ecommerce.exceptions;

import com.app.ecommerce.enums.ErrorCode;
import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    private final ErrorCode errorCode;
    protected ApiException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}