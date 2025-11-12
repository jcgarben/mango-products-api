package com.mango.products.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Nested
    class FactoryMethodsTest {

        @Test
        void givenValidParameters_whenCreatingProduct_thenShouldCreateWithoutId() {
            // Given
            String name = "Test Product";
            String description = "Test Description";

            // When
            Product product = Product.create(name, description);

            // Then
            assertNull(product.getId());
            assertEquals(name, product.getName());
            assertEquals(description, product.getDescription());
        }

        @Test
        void givenValidParametersWithId_whenCreatingProduct_thenShouldCreateWithId() {
            // Given
            Long id = 1L;
            String name = "Test Product";
            String description = "Test Description";

            // When
            Product product = Product.of(id, name, description);

            // Then
            assertEquals(id, product.getId());
            assertEquals(name, product.getName());
            assertEquals(description, product.getDescription());
        }

        @Test
        void givenNullDescription_whenCreatingProduct_thenShouldAccept() {
            // Given
            String name = "Test Product";

            // When
            Product product = Product.create(name, null);

            // Then
            assertNotNull(product);
            assertEquals(name, product.getName());
            assertNull(product.getDescription());
        }
    }

    @Nested
    class ValidationTest {

        @Test
        void givenNullName_whenCreatingProduct_thenShouldThrowException() {
            // When & Then
            assertThrows(NullPointerException.class, () ->
                    Product.create(null, "Description")
            );
        }
    }

    @Nested
    class EqualsAndHashCodeTest {

        @Test
        void givenSameId_whenComparingProducts_thenShouldBeEqual() {
            // Given
            Product product1 = Product.of(1L, "Product A", "Description A");
            Product product2 = Product.of(1L, "Product B", "Description B");

            // When & Then
            assertEquals(product1, product2);
            assertEquals(product1.hashCode(), product2.hashCode());
        }

        @Test
        void givenDifferentId_whenComparingProducts_thenShouldNotBeEqual() {
            // Given
            Product product1 = Product.of(1L, "Product", "Description");
            Product product2 = Product.of(2L, "Product", "Description");

            // When & Then
            assertNotEquals(product1, product2);
        }

        @Test
        void givenSameInstance_whenComparingProduct_thenShouldBeEqual() {
            // Given
            Product product = Product.create("Product", "Description");

            // When & Then
            assertEquals(product, product);
        }
    }

    @Nested
    class SettersTest {

        @Test
        void givenNewId_whenSettingId_thenShouldUpdateSuccessfully() {
            // Given
            Product product = Product.create("Product", "Description");
            Long newId = 100L;

            // When
            product.setId(newId);

            // Then
            assertEquals(newId, product.getId());
        }
    }
}

