package com.mango.products.domain.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PriceOverlapExceptionTest {

    @Test
    void givenPriceData_whenCreatingException_thenShouldBeRuntimeExceptionWithDescriptiveMessage() {
        // Given
        Long productId = 123L;
        LocalDate initDate = LocalDate.of(2025, 1, 15);
        LocalDate endDate = LocalDate.of(2025, 2, 15);

        // When
        PriceOverlapException exception = new PriceOverlapException(productId, initDate, endDate);

        // Then
        assertInstanceOf(RuntimeException.class, exception);

        String message = exception.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
        assertTrue(message.contains("123"));
        assertTrue(message.contains("2025-01-15"));
        assertTrue(message.contains("2025-02-15"));

        String lowerMessage = message.toLowerCase();
        assertTrue(lowerMessage.contains("price"));
        assertTrue(lowerMessage.contains("already") || lowerMessage.contains("exists") ||
                lowerMessage.contains("overlap"));
    }

    @Test
    void givenOpenEndedPrice_whenCreatingException_thenShouldHandleNullEndDate() {
        // Given
        Long productId = 456L;
        LocalDate initDate = LocalDate.of(2025, 1, 15);
        LocalDate endDate = null;

        // When
        PriceOverlapException exception = new PriceOverlapException(productId, initDate, endDate);

        // Then
        assertInstanceOf(RuntimeException.class, exception);

        String message = exception.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
        assertTrue(message.contains("456"));
        assertTrue(message.contains("2025-01-15"));
    }
}

