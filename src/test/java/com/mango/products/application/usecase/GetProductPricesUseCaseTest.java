package com.mango.products.application.usecase;

import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        Price price1 = Price.of(1L, productId, BigDecimal.valueOf(10.99),
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        Price price2 = Price.of(2L, productId, BigDecimal.valueOf(12.99),
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
    void givenExistingProductAndDate_whenGettingCurrentPrice_thenShouldReturnPrice() {
        // Given
        Long productId = 1L;
        LocalDate date = LocalDate.of(2025, 1, 15);
        Price price = Price.of(1L, productId, BigDecimal.valueOf(10.99),
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

        when(productRepository.existsById(productId)).thenReturn(true);
        when(priceRepository.findByProductIdAndDate(productId, date)).thenReturn(Optional.of(price));

        // When
        Optional<Price> result = getProductPricesUseCase.getCurrentPrice(productId, date);

        // Then
        assertTrue(result.isPresent());
        assertEquals(price.getId(), result.get().getId());
        assertEquals(price.getValue(), result.get().getValue());

        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, times(1)).findByProductIdAndDate(productId, date);
    }

    @Test
    void givenExistingProductButNoPriceOnDate_whenGettingCurrentPrice_thenShouldReturnEmpty() {
        // Given
        Long productId = 1L;
        LocalDate date = LocalDate.of(2025, 3, 15);

        when(productRepository.existsById(productId)).thenReturn(true);
        when(priceRepository.findByProductIdAndDate(productId, date)).thenReturn(Optional.empty());

        // When
        Optional<Price> result = getProductPricesUseCase.getCurrentPrice(productId, date);

        // Then
        assertFalse(result.isPresent());

        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, times(1)).findByProductIdAndDate(productId, date);
    }

    @Test
    void givenNonExistingProduct_whenGettingCurrentPrice_thenShouldThrowException() {
        // Given
        Long productId = 999L;
        LocalDate date = LocalDate.of(2025, 1, 15);

        when(productRepository.existsById(productId)).thenReturn(false);

        // When & Then
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> getProductPricesUseCase.getCurrentPrice(productId, date)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).existsById(productId);
        verify(priceRepository, never()).findByProductIdAndDate(any(), any());
    }
}
