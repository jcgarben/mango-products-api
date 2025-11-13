package com.mango.products.application.port.exception;

public class RepositoryConstraintViolationException extends RuntimeException {

    public RepositoryConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}

