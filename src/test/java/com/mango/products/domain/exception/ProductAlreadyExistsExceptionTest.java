package com.mango.products.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductAlreadyExistsExceptionTest {

    @Test
    void givenProductName_whenCreatingException_thenShouldBeRuntimeExceptionWithDescriptiveMessage() {
        // Given
        String productName = "Test Product";

        // When
        ProductAlreadyExistsException exception = new ProductAlreadyExistsException(productName);

        // Then
        assertInstanceOf(RuntimeException.class, exception);

        String message = exception.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
        assertTrue(message.contains(productName));
        assertTrue(message.toLowerCase().contains("already") ||
                message.toLowerCase().contains("exists"));
    }
}

