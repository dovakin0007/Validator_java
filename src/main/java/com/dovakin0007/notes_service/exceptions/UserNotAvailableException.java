package com.dovakin0007.notes_service.exceptions;

public class UserNotAvailableException extends RuntimeException {
    public UserNotAvailableException(String message) {
        super(message);
    }
}
