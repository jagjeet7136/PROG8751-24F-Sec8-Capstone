package com.app.ecommerce.exceptions;

public class BadRequestException extends Exception {
    public BadRequestException() {}
    public BadRequestException(String message) {
        super(message);
    }
}
