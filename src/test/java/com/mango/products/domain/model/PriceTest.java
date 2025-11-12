package com.mango.products.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PriceTest {

    @Nested
    class FactoryMethodsTest {

        @Test
        void givenValidParameters_whenCreatingPrice_thenShouldCreateWithoutId() {
            // Given
            Long productId = 1L;
            BigDecimal value = BigDecimal.valueOf(10.00);
            LocalDate initDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            // When
            Price price = Price.create(productId, value, initDate, endDate);

            // Then
            assertNull(price.getId());
            assertEquals(productId, price.getProductId());
            assertEquals(value, price.getValue());
            assertEquals(initDate, price.getInitDate());
            assertEquals(endDate, price.getEndDate());
        }

        @Test
        void givenValidParametersWithId_whenCreatingPrice_thenShouldCreateWithId() {
            // Given
            Long id = 10L;
            Long productId = 1L;
            BigDecimal value = BigDecimal.valueOf(10.00);
            LocalDate initDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            // When
            Price price = Price.of(id, productId, value, initDate, endDate);

            // Then
            assertEquals(id, price.getId());
            assertEquals(productId, price.getProductId());
            assertEquals(value, price.getValue());
            assertEquals(initDate, price.getInitDate());
            assertEquals(endDate, price.getEndDate());
        }

        @Test
        void givenNullEndDate_whenCreatingPrice_thenShouldCreateOpenEndedPrice() {
            // Given
            Long productId = 1L;
            BigDecimal value = BigDecimal.valueOf(10.00);
            LocalDate initDate = LocalDate.of(2025, 1, 1);

            // When
            Price price = Price.create(productId, value, initDate, null);

            // Then
            assertNull(price.getEndDate());
            assertEquals(initDate, price.getInitDate());
        }
    }

    @Nested
    class ValidationTest {

        @Test
        void givenNullProductId_whenCreatingPrice_thenShouldThrowException() {
            // Given
            BigDecimal value = BigDecimal.valueOf(10.00);
            LocalDate initDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            // When & Then
            assertThrows(NullPointerException.class, () ->
                    Price.create(null, value, initDate, endDate)
            );
        }

        @Test
        void givenNullValue_whenCreatingPrice_thenShouldThrowException() {
            // Given
            Long productId = 1L;
            LocalDate initDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            // When & Then
            assertThrows(NullPointerException.class, () ->
                    Price.create(productId, null, initDate, endDate)
            );
        }

        @Test
        void givenZeroValue_whenCreatingPrice_thenShouldThrowException() {
            // Given
            Long productId = 1L;
            BigDecimal value = BigDecimal.ZERO;
            LocalDate initDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Price.create(productId, value, initDate, endDate)
            );
            assertTrue(exception.getMessage().contains("greater than zero"));
        }

        @Test
        void givenNegativeValue_whenCreatingPrice_thenShouldThrowException() {
            // Given
            Long productId = 1L;
            BigDecimal value = BigDecimal.valueOf(-10.00);
            LocalDate initDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Price.create(productId, value, initDate, endDate)
            );
            assertTrue(exception.getMessage().contains("greater than zero"));
        }

        @Test
        void givenNullInitDate_whenCreatingPrice_thenShouldThrowException() {
            // Given
            Long productId = 1L;
            BigDecimal value = BigDecimal.valueOf(10.00);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            // When & Then
            assertThrows(NullPointerException.class, () ->
                    Price.create(productId, value, null, endDate)
            );
        }

        @Test
        void givenEndDateBeforeInitDate_whenCreatingPrice_thenShouldThrowException() {
            // Given
            Long productId = 1L;
            BigDecimal value = BigDecimal.valueOf(10.00);
            LocalDate initDate = LocalDate.of(2025, 1, 31);
            LocalDate endDate = LocalDate.of(2025, 1, 1);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Price.create(productId, value, initDate, endDate)
            );
            assertTrue(exception.getMessage().contains("after or equal"));
        }

        @Test
        void givenEndDateEqualsInitDate_whenCreatingPrice_thenShouldAccept() {
            // Given
            Long productId = 1L;
            BigDecimal value = BigDecimal.valueOf(10.00);
            LocalDate date = LocalDate.of(2025, 1, 1);

            // When & Then
            assertDoesNotThrow(() ->
                    Price.create(productId, value, date, date)
            );
        }
    }

    @Nested
    class OverlapsTest {

        @Test
        void givenSequentialRanges_whenCheckingOverlap_thenShouldNotOverlap() {
            // Given: Two sequential prices without gap
            Price price1 = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price price2 = Price.create(1L, BigDecimal.valueOf(12.00),
                    LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));

            // When & Then
            assertFalse(price1.overlaps(price2));
            assertFalse(price2.overlaps(price1));
        }

        @Test
        void givenIntersectingRanges_whenCheckingOverlap_thenShouldOverlap() {
            // Given: Two prices with overlapping ranges
            Price price1 = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price price2 = Price.create(1L, BigDecimal.valueOf(12.00),
                    LocalDate.of(2025, 1, 15), LocalDate.of(2025, 2, 15));

            // When & Then
            assertTrue(price1.overlaps(price2));
            assertTrue(price2.overlaps(price1));
        }

        @Test
        void givenOneRangeContainsAnother_whenCheckingOverlap_thenShouldOverlap() {
            // Given: One price completely contains another
            Price price1 = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

            Price price2 = Price.create(1L, BigDecimal.valueOf(12.00),
                    LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 31));

            // When & Then
            assertTrue(price1.overlaps(price2));
            assertTrue(price2.overlaps(price1));
        }

        @Test
        void givenFirstPriceIsOpenEnded_whenCheckingOverlap_thenShouldOverlap() {
            // Given: First price without end date (open-ended)
            Price price1 = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), null);

            Price price2 = Price.create(1L, BigDecimal.valueOf(12.00),
                    LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

            // When & Then
            assertTrue(price1.overlaps(price2));
            assertTrue(price2.overlaps(price1));
        }

        @Test
        void givenBothPricesAreOpenEnded_whenCheckingOverlap_thenShouldOverlap() {
            // Given: Both prices without end date
            Price price1 = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), null);

            Price price2 = Price.create(1L, BigDecimal.valueOf(12.00),
                    LocalDate.of(2025, 6, 1), null);

            // When & Then
            assertTrue(price1.overlaps(price2));
            assertTrue(price2.overlaps(price1));
        }

        @Test
        void givenOpenEndedPriceStartsAfterClosedEnds_whenCheckingOverlap_thenShouldNotOverlap() {
            // Given: Open-ended price starts after closed price ends
            Price price1 = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price price2 = Price.create(1L, BigDecimal.valueOf(12.00),
                    LocalDate.of(2025, 2, 1), null);

            // When & Then
            assertFalse(price1.overlaps(price2));
            assertFalse(price2.overlaps(price1));
        }

        @Test
        void givenDifferentProducts_whenCheckingOverlap_thenShouldNotOverlap() {
            // Given: Two prices with same dates but different products
            Price price1 = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price price2 = Price.create(2L, BigDecimal.valueOf(12.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            // When & Then
            assertFalse(price1.overlaps(price2));
            assertFalse(price2.overlaps(price1));
        }

        @Test
        void givenNullOtherPrice_whenCheckingOverlap_thenShouldNotOverlap() {
            // Given
            Price price = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            // When & Then
            assertFalse(price.overlaps(null));
        }

        @Test
        void givenRangesShareOneDay_whenCheckingOverlap_thenShouldOverlap() {
            // Given: Ranges that share only the boundary date
            Price price1 = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15));

            Price price2 = Price.create(1L, BigDecimal.valueOf(12.00),
                    LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 31));

            // When & Then
            assertTrue(price1.overlaps(price2));
            assertTrue(price2.overlaps(price1));
        }
    }

    @Nested
    class EqualsAndHashCodeTest {

        @Test
        void givenSameId_whenComparingPrices_thenShouldBeEqual() {
            // Given
            Price price1 = Price.of(1L, 100L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price price2 = Price.of(1L, 200L, BigDecimal.valueOf(20.00),
                    LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));

            // When & Then
            assertEquals(price1, price2);
            assertEquals(price1.hashCode(), price2.hashCode());
        }

        @Test
        void givenDifferentId_whenComparingPrices_thenShouldNotBeEqual() {
            // Given
            Price price1 = Price.of(1L, 100L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            Price price2 = Price.of(2L, 100L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            // When & Then
            assertNotEquals(price1, price2);
        }

        @Test
        void givenSameInstance_whenComparingPrice_thenShouldBeEqual() {
            // Given
            Price price = Price.create(1L, BigDecimal.valueOf(10.00),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

            // When & Then
            assertEquals(price, price);
        }
    }
}
