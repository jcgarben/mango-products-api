package com.mango.products.application.usecase;

import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.ProductAlreadyExistsException;
import com.mango.products.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @Test
    void givenValidProductData_whenCreatingProduct_thenShouldSaveAndReturnProduct() {
        // Given
        String name = "Product";
        String description = "Product description";
        Product createdProduct = Product.of(1L, name, description);

        when(productRepository.save(any(Product.class))).thenReturn(createdProduct);

        // When
        Product result = createProductUseCase.execute(name, description);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void givenNullDescription_whenCreatingProduct_thenShouldSaveWithNullDescription() {
        // Given
        String name = "Product";
        Product createdProduct = Product.of(1L, name, null);

        when(productRepository.save(any(Product.class))).thenReturn(createdProduct);

        // When
        Product result = createProductUseCase.execute(name, null);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertNull(result.getDescription());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void givenDuplicateProductName_whenCreatingProduct_thenShouldThrowException() {
        // Given
        String name = "Product";
        String description = "Product description";

        when(productRepository.save(any(Product.class)))
            .thenThrow(new ProductAlreadyExistsException(name));

        // When & Then
        ProductAlreadyExistsException exception = assertThrows(
            ProductAlreadyExistsException.class,
            () -> createProductUseCase.execute(name, description)
        );

        assertTrue(exception.getMessage().contains(name));
        verify(productRepository, times(1)).save(any(Product.class));
    }
}