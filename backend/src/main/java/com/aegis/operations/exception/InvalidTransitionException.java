package com.aegis.operations.exception;

public class InvalidTransitionException extends RuntimeException {
    private final String code;

    public InvalidTransitionException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
