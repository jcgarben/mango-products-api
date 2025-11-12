package com.mango.products.domain.exception;

public class InvalidCurrencyException extends RuntimeException {

    public InvalidCurrencyException(String currencyCode) {
        super("Invalid currency code: " + currencyCode + ". Must be a valid ISO 4217 currency code (e.g., EUR, USD, GBP)");
    }
}

