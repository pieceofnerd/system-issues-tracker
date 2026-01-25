package com.example.systemissuestracker.exception;

public class PreexistingIdException extends IllegalArgumentException {
    public PreexistingIdException(String message) {
        super(message);
    }
}
