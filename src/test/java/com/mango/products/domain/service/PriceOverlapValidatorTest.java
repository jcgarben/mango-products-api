package com.mango.products.domain.service;

import com.mango.products.domain.exception.PriceOverlapException;
import com.mango.products.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceOverlapValidatorTest {

    private PriceOverlapValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PriceOverlapValidator();
    }

    @Nested
    class ValidScenariosTest {

        @Test
        void givenNoExistingPrices_whenValidating_thenShouldNotThrowException() {
            // Given
            Price newPrice = Price.create(1L, BigDecimal.valueOf(10.00),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            // When & Then
            assertDoesNotThrow(() -> validator.validate(newPrice, Collections.emptyList()));
        }


        @Test
        void givenNonOverlappingPrices_whenValidating_thenShouldNotThrowException() {
            // Given: Sequential prices without overlap
            Price existingPrice = Price.of(1L, 1L, BigDecimal.valueOf(10.00),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price newPrice = Price.create(1L, BigDecimal.valueOf(12.00),
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));

            List<Price> existingPrices = Collections.singletonList(existingPrice);

            // When & Then
            assertDoesNotThrow(() -> validator.validate(newPrice, existingPrices));
        }

        @Test
        void givenDifferentProducts_whenValidating_thenShouldNotThrowException() {
            // Given: Same date ranges but different products
            Price existingPrice = Price.of(1L, 1L, BigDecimal.valueOf(10.00),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price newPrice = Price.create(2L, BigDecimal.valueOf(12.00),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            List<Price> existingPrices = Collections.singletonList(existingPrice);

            // When & Then
            assertDoesNotThrow(() -> validator.validate(newPrice, existingPrices));
        }

        @Test
        void givenNewPriceFitsBetween_whenValidating_thenShouldNotThrowException() {
            // Given: New price fits in a gap between two existing prices
            Price existingPrice1 = Price.of(1L, 1L, BigDecimal.valueOf(10.00),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price existingPrice2 = Price.of(2L, 1L, BigDecimal.valueOf(12.00),
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 31));

            Price newPrice = Price.create(1L, BigDecimal.valueOf(11.00),
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));

            List<Price> existingPrices = Arrays.asList(existingPrice1, existingPrice2);

            // When & Then
            assertDoesNotThrow(() -> validator.validate(newPrice, existingPrices));
        }
    }

    @Nested
    class InvalidScenariosTest {

        @Test
        void givenOverlappingPrices_whenValidating_thenShouldThrowException() {
            // Given: Overlapping prices
            Price existingPrice = Price.of(1L, 1L, BigDecimal.valueOf(10.00),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price newPrice = Price.create(1L, BigDecimal.valueOf(12.00),
                LocalDate.of(2025, 1, 15), LocalDate.of(2025, 2, 15));

            List<Price> existingPrices = Collections.singletonList(existingPrice);

            // When & Then
            PriceOverlapException exception = assertThrows(
                PriceOverlapException.class,
                () -> validator.validate(newPrice, existingPrices)
            );

            assertTrue(exception.getMessage().contains("1"));
            assertTrue(exception.getMessage().contains("2025-01-15"));
        }


        @Test
        void givenOverlapWithMultiplePrices_whenValidating_thenShouldThrowException() {
            // Given: New price overlaps with one of several existing prices
            Price existingPrice1 = Price.of(1L, 1L, BigDecimal.valueOf(10.00),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price existingPrice2 = Price.of(2L, 1L, BigDecimal.valueOf(11.00),
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));

            Price existingPrice3 = Price.of(3L, 1L, BigDecimal.valueOf(12.00),
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 31));

            Price newPrice = Price.create(1L, BigDecimal.valueOf(15.00),
                LocalDate.of(2025, 2, 15), LocalDate.of(2025, 3, 15));

            List<Price> existingPrices = Arrays.asList(existingPrice1, existingPrice2, existingPrice3);

            // When & Then
            assertThrows(
                PriceOverlapException.class,
                () -> validator.validate(newPrice, existingPrices)
            );
        }

        @Test
        void givenNewPriceOverlapsOpenEnded_whenValidating_thenShouldThrowException() {
            // Given: Existing open-ended price
            Price existingPrice = Price.of(1L, 1L, BigDecimal.valueOf(10.00),
                LocalDate.of(2025, 1, 1), null);

            Price newPrice = Price.create(1L, BigDecimal.valueOf(12.00),
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

            List<Price> existingPrices = Collections.singletonList(existingPrice);

            // When & Then
            assertThrows(
                PriceOverlapException.class,
                () -> validator.validate(newPrice, existingPrices)
            );
        }

        @Test
        void givenBothPricesOpenEnded_whenValidating_thenShouldThrowException() {
            // Given: Both prices are open-ended
            Price existingPrice = Price.of(1L, 1L, BigDecimal.valueOf(10.00),
                LocalDate.of(2025, 1, 1), null);

            Price newPrice = Price.create(1L, BigDecimal.valueOf(12.00),
                LocalDate.of(2025, 6, 1), null);

            List<Price> existingPrices = Collections.singletonList(existingPrice);

            // When & Then
            assertThrows(
                PriceOverlapException.class,
                () -> validator.validate(newPrice, existingPrices)
            );
        }
    }
}
