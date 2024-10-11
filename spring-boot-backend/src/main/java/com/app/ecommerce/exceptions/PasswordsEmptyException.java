package com.app.todolist.exceptions;

public class PasswordsEmptyException extends RuntimeException{
    public PasswordsEmptyException(String message) {
        super(message);
    }
}
