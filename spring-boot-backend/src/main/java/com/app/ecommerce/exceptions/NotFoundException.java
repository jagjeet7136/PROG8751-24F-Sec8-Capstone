package com.app.ecommerce.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException() {}
    public NotFoundException(String message) {
        super(message);
    }
}
