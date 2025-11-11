package com.mango.products.infrastructure.rest.mapper;

import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.rest.dto.ProductResponse;

public class ProductDtoMapper {


    public static ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        return response;
    }
}