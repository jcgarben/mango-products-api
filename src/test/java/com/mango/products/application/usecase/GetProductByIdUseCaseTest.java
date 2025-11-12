package com.mango.products.application.usecase;

import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductByIdUseCase getProductByIdUseCase;

    @Test
    void givenExistingProductId_whenGettingProduct_thenShouldReturnProduct() {
        // Given
        Long productId = 1L;
        Product product = Product.of(productId, "Product", "Product Description");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        Product result = getProductByIdUseCase.execute(productId);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Product", result.getName());
        assertEquals("Product Description", result.getDescription());

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void givenNonExistingProductId_whenGettingProduct_thenShouldThrowException() {
        // Given
        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> getProductByIdUseCase.execute(productId)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById(productId);
    }
}

