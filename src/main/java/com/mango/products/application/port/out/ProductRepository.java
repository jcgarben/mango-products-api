package com.mango.products.application.port.out;

import com.mango.products.domain.model.Product;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    boolean existsById(Long id);
}

