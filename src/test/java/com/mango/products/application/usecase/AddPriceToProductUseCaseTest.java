package com.mango.products.application.usecase;

import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.PriceOverlapException;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.service.PriceOverlapValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddPriceToProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private PriceOverlapValidator overlapValidator;

    @InjectMocks
    private AddPriceToProductUseCase addPriceToProductUseCase;

    @Test
    void givenValidPriceDataAndNoOverlapping_whenAddingPrice_thenShouldSaveAndReturnPrice() {
        // Given
        Long productId = 1L;
        BigDecimal value = BigDecimal.valueOf(10.99);
        String currencyCode = "EUR";
        Currency currency = Currency.getInstance(currencyCode);
        LocalDate initDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        Product product = Product.of(productId, "Product", "Product Description");
        Price savedPrice = Price.of(1L, productId, value, currency, initDate, endDate);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceRepository.findByProductIdAndCurrency(productId, currencyCode)).thenReturn(Collections.emptyList());
        when(priceRepository.save(any(Price.class))).thenReturn(savedPrice);
        doNothing().when(overlapValidator).validate(any(Price.class), anyList());

        // When
        Price result = addPriceToProductUseCase.execute(productId, value, currencyCode, initDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(productId, result.getProductId());
        assertEquals(value, result.getValue());
        assertEquals(initDate, result.getInitDate());
        assertEquals(endDate, result.getEndDate());

        verify(productRepository, times(1)).findById(productId);
        verify(priceRepository, times(1)).findByProductIdAndCurrency(productId, currencyCode);
        verify(overlapValidator, times(1)).validate(any(Price.class), anyList());
        verify(priceRepository, times(1)).save(any(Price.class));
    }

    @Test
    void givenNonExistingProduct_whenAddingPrice_thenShouldThrowException() {
        // Given
        Long productId = 999L;
        BigDecimal value = BigDecimal.valueOf(10.99);
        String currencyCode = "EUR";
        LocalDate initDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> addPriceToProductUseCase.execute(productId, value, currencyCode, initDate, endDate)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById(productId);
        verify(priceRepository, never()).findByProductIdAndCurrency(any(), any());
        verify(overlapValidator, never()).validate(any(), any());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void givenOverlappingPrice_whenAddingPrice_thenShouldThrowException() {
        // Given
        Long productId = 1L;
        BigDecimal value = BigDecimal.valueOf(10.99);
        String currencyCode = "EUR";
        Currency currency = Currency.getInstance(currencyCode);
        LocalDate initDate = LocalDate.of(2025, 1, 15);
        LocalDate endDate = LocalDate.of(2025, 2, 15);

        Product product = Product.of(productId, "Product", "Product Description");
        Price existingPrice = Price.of(1L, productId, BigDecimal.valueOf(9.99), currency,
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceRepository.findByProductIdAndCurrency(productId, currencyCode)).thenReturn(Collections.singletonList(existingPrice));
        doThrow(new PriceOverlapException(productId, initDate, endDate))
            .when(overlapValidator).validate(any(Price.class), anyList());

        // When & Then
        PriceOverlapException exception = assertThrows(
            PriceOverlapException.class,
            () -> addPriceToProductUseCase.execute(productId, value, currencyCode, initDate, endDate)
        );

        assertTrue(exception.getMessage().contains("1"));
        verify(productRepository, times(1)).findById(productId);
        verify(priceRepository, times(1)).findByProductIdAndCurrency(productId, currencyCode);
        verify(overlapValidator, times(1)).validate(any(Price.class), anyList());
        verify(priceRepository, never()).save(any());
    }

    @Test
    void givenOpenEndedPrice_whenAddingPrice_thenShouldSaveSuccessfully() {
        // Given
        Long productId = 1L;
        BigDecimal value = BigDecimal.valueOf(10.99);
        String currencyCode = "EUR";
        Currency currency = Currency.getInstance(currencyCode);
        LocalDate initDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = null;

        Product product = Product.of(productId, "Product", "Product Description");
        Price savedPrice = Price.of(1L, productId, value, currency, initDate, endDate);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceRepository.findByProductIdAndCurrency(productId, currencyCode)).thenReturn(Collections.emptyList());
        when(priceRepository.save(any(Price.class))).thenReturn(savedPrice);
        doNothing().when(overlapValidator).validate(any(Price.class), anyList());

        // When
        Price result = addPriceToProductUseCase.execute(productId, value, currencyCode, initDate, endDate);

        // Then
        assertNotNull(result);
        assertNull(result.getEndDate());

        verify(overlapValidator, times(1)).validate(any(Price.class), anyList());
        verify(priceRepository, times(1)).save(any(Price.class));
    }
}

