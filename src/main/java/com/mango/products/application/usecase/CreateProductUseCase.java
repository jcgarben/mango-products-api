package com.mango.products.application.usecase;

import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(String name, String description) {
        Product product = Product.create(name, description);
        return productRepository.save(product);
    }
}