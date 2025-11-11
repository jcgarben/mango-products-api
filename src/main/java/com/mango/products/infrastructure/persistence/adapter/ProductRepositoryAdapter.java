package com.mango.products.infrastructure.persistence.adapter;

import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.ProductAlreadyExistsException;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.persistence.entity.ProductEntity;
import com.mango.products.infrastructure.persistence.mapper.ProductMapper;
import com.mango.products.infrastructure.persistence.repository.JpaProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaRepository;

    public ProductRepositoryAdapter(JpaProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        try {
            ProductEntity entity = ProductMapper.toEntity(product);
            ProductEntity saved = jpaRepository.save(entity);
            return ProductMapper.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ProductAlreadyExistsException(product.getName());
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id)
            .map(ProductMapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}

