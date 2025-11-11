package com.mango.products.infrastructure.persistence.mapper;

import com.mango.products.domain.model.Price;
import com.mango.products.infrastructure.persistence.entity.PriceEntity;

public class PriceMapper {

    public static Price toDomain(PriceEntity entity) {
        if (entity == null) {
            return null;
        }
        return Price.of(
                entity.getId(),
                entity.getProductId(),
                entity.getValue(),
                entity.getInitDate(),
                entity.getEndDate()
        );
    }

    public static PriceEntity toEntity(Price domain) {
        if (domain == null) {
            return null;
        }
        PriceEntity entity = new PriceEntity(
                domain.getProductId(),
                domain.getValue(),
                domain.getInitDate(),
                domain.getEndDate()
        );
        entity.setId(domain.getId());
        return entity;
    }
}
