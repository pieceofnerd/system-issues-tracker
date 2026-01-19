package com.example.systemissuestracker.exception;

public class CorruptedIdException extends IllegalStateException {
    public CorruptedIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
