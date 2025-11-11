package com.mango.products.domain.exception;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String productName) {
        super("A product already exists with name: " + productName);
    }
}