package com.mango.products.application.usecase;

import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductPricesUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private GetProductPricesUseCase getProductPricesUseCase;

    @Test
    void givenExistingProductWithPrices_whenGettingAllPrices_thenShouldReturnAllPrices() {
        // Given
        Long productId = 1L;
        Currency currency = Currency.getInstance("EUR");
        Price price1 = Price.of(1L, productId, BigDecimal.valueOf(10.99), currency,
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        Price price2 = Price.of(2L, productId, BigDecimal.valueOf(12.99), currency,
            LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));
        List<Price> prices = Arrays.asList(price1, price2);

        when(productRepository.existsById(productId)).thenReturn(true);
        when(priceRepository.findByProductId(productId)).thenReturn(prices);

        // When
        List<Price> result = getProductPricesUseCase.getAllPrices(productId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(price1.getId(), result.get(0).getId());
        assertEquals(price2.getId(), result.get(1).getId());

        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, times(1)).findByProductId(productId);
    }

    @Test
    void givenExistingProductWithoutPrices_whenGettingAllPrices_thenShouldReturnEmptyList() {
        // Given
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);
        when(priceRepository.findByProductId(productId)).thenReturn(Collections.emptyList());

        // When
        List<Price> result = getProductPricesUseCase.getAllPrices(productId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, times(1)).findByProductId(productId);
    }

    @Test
    void givenNonExistingProduct_whenGettingAllPrices_thenShouldThrowException() {
        // Given
        Long productId = 999L;

        when(productRepository.existsById(productId)).thenReturn(false);

        // When & Then
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> getProductPricesUseCase.getAllPrices(productId)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, never()).findByProductId(any());
    }

    @Test
    void givenExistingProductAndDate_whenGettingCurrentPrices_thenShouldReturnPrices() {
        // Given
        Long productId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 15);
        Currency currency = Currency.getInstance("EUR");
        Price price = Price.of(1L, productId, BigDecimal.valueOf(10.99), currency,
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

        when(productRepository.existsById(productId)).thenReturn(true);
        when(priceRepository.findByProductIdAndDate(productId, date)).thenReturn(Collections.singletonList(price));

        // When
        List<Price> result = getProductPricesUseCase.getCurrentPrices(productId, date);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(price.getId(), result.get(0).getId());
        assertEquals(price.getValue(), result.get(0).getValue());

        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, times(1)).findByProductIdAndDate(productId, date);
    }

    @Test
    void givenExistingProductButNoPriceOnDate_whenGettingCurrentPrices_thenShouldReturnEmptyList() {
        // Given
        Long productId = 1L;
        LocalDate date = LocalDate.of(2025, 3, 15);

        when(productRepository.existsById(productId)).thenReturn(true);
        when(priceRepository.findByProductIdAndDate(productId, date)).thenReturn(Collections.emptyList());

        // When
        List<Price> result = getProductPricesUseCase.getCurrentPrices(productId, date);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, times(1)).findByProductIdAndDate(productId, date);
    }

    @Test
    void givenNonExistingProduct_whenGettingCurrentPrices_thenShouldThrowException() {
        // Given
        Long productId = 999L;
        LocalDate date = LocalDate.of(2025, 1, 15);

        when(productRepository.existsById(productId)).thenReturn(false);

        // When & Then
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> getProductPricesUseCase.getCurrentPrices(productId, date)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, never()).findByProductIdAndDate(any(), any());
    }

    @Test
    void givenMultipleCurrenciesOnDate_whenGettingCurrentPrices_thenShouldReturnAllPrices() {
        // Given
        Long productId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 15);
        Currency eur = Currency.getInstance("EUR");
        Currency usd = Currency.getInstance("USD");

        Price priceEur = Price.of(1L, productId, BigDecimal.valueOf(10.99), eur,
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        Price priceUsd = Price.of(2L, productId, BigDecimal.valueOf(12.99), usd,
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

        when(productRepository.existsById(productId)).thenReturn(true);
        when(priceRepository.findByProductIdAndDate(productId, date))
            .thenReturn(Arrays.asList(priceEur, priceUsd));

        // When
        List<Price> result = getProductPricesUseCase.getCurrentPrices(productId, date);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(priceEur.getId(), result.get(0).getId());
        assertEquals(priceUsd.getId(), result.get(1).getId());
        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, times(1)).findByProductIdAndDate(productId, date);
    }
}
