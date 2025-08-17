package com.dovakin0007.notes_service.exceptions;

public class UserCreationFailedException extends RuntimeException {
    public UserCreationFailedException(String message, Throwable t) {
        super(message, t);
    }
}
