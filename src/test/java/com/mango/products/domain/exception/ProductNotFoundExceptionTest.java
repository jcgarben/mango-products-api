package com.mango.products.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductNotFoundExceptionTest {

    @Test
    void givenProductId_whenCreatingException_thenShouldBeRuntimeExceptionWithDescriptiveMessage() {
        // Given
        Long productId = 123L;

        // When
        ProductNotFoundException exception = new ProductNotFoundException(productId);

        // Then
        assertInstanceOf(RuntimeException.class, exception);

        String message = exception.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
        assertTrue(message.contains("123"));
        assertTrue(message.toLowerCase().contains("product"));
        assertTrue(message.toLowerCase().contains("not found"));
    }
}

