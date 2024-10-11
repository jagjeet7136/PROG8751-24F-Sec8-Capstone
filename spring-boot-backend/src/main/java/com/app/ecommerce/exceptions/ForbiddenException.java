package com.app.ecommerce.exceptions;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message) {
        super(message);
    }
}
