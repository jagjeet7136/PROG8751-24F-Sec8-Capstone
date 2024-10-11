package com.app.ecommerce.exceptions;

public class PasswordsEmptyException extends RuntimeException{
    public PasswordsEmptyException(String message) {
        super(message);
    }
}
