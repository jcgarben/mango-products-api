package com.mango.products.infrastructure.rest.controller;

import com.mango.products.application.usecase.CreateProductUseCase;
import com.mango.products.application.usecase.GetProductByIdUseCase;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.rest.api.ProductsApi;
import com.mango.products.infrastructure.rest.dto.CreateProductRequest;
import com.mango.products.infrastructure.rest.dto.ProductResponse;
import com.mango.products.infrastructure.rest.mapper.ProductDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = createProductUseCase.execute(request.getName(), request.getDescription());
        ProductResponse response = ProductDtoMapper.toResponse(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long id) {
        Product product = getProductByIdUseCase.execute(id);
        ProductResponse response = ProductDtoMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }
}
