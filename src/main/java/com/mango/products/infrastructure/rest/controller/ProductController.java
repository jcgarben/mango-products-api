package com.mango.products.infrastructure.rest.controller;

import com.mango.products.application.usecase.CreateProductUseCase;
import com.mango.products.application.usecase.GetProductByIdUseCase;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.rest.api.ProductsApi;
import com.mango.products.infrastructure.rest.dto.CreateProductRequest;
import com.mango.products.infrastructure.rest.dto.ProductResponse;
import com.mango.products.infrastructure.rest.mapper.ProductDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductsApi {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                           GetProductByIdUseCase getProductByIdUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    @Override
    public ResponseEntity<ProductResponse> createProduct(CreateProductRequest request) {
        Product product = createProductUseCase.execute(request.getName(), request.getDescription());
        ProductResponse response = ProductDtoMapper.toResponse(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ProductResponse> getProductById(Long id) {
        Product product = getProductByIdUseCase.execute(id);
        ProductResponse response = ProductDtoMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }
}
