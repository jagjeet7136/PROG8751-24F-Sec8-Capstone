package com.app.ecommerce.exceptions;

public class ValidationException extends Exception {
    public ValidationException() {}
    public ValidationException(String message) {
        super(message);
    }
}
