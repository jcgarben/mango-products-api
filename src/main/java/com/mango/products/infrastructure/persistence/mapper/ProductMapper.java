package com.mango.products.infrastructure.persistence.mapper;

import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.persistence.entity.ProductEntity;

public class ProductMapper {

    public static Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return Product.of(
            entity.getId(),
            entity.getName(),
            entity.getDescription()
        );
    }

    public static ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        ProductEntity entity = new ProductEntity(
            domain.getName(),
            domain.getDescription()
        );
        entity.setId(domain.getId());
        return entity;
    }
}

