package com.mango.products.domain.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PriceNotFoundExceptionTest {

    @Test
    void givenProductIdAndDate_whenCreatingException_thenShouldBeRuntimeExceptionWithDescriptiveMessage() {
        // Given
        Long productId = 123L;
        LocalDate date = LocalDate.of(2025, 1, 15);

        // When
        PriceNotFoundException exception = new PriceNotFoundException(productId, date);

        // Then
        assertInstanceOf(RuntimeException.class, exception);

        String message = exception.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
        assertTrue(message.contains("123"));
        assertTrue(message.contains("2025-01-15"));

        String lowerMessage = message.toLowerCase();
        assertTrue(lowerMessage.contains("price"));
        assertTrue(lowerMessage.contains("not found") || lowerMessage.contains("no"));
    }
}
