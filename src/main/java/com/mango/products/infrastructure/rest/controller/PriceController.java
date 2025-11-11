package com.mango.products.infrastructure.rest.controller;

import com.mango.products.application.usecase.AddPriceToProductUseCase;
import com.mango.products.application.usecase.GetProductByIdUseCase;
import com.mango.products.application.usecase.GetProductPricesUseCase;
import com.mango.products.domain.exception.PriceNotFoundException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.rest.api.PricesApi;
import com.mango.products.infrastructure.rest.dto.AddPriceRequest;
import com.mango.products.infrastructure.rest.dto.GetProductPrices200Response;
import com.mango.products.infrastructure.rest.dto.PriceResponse;
import com.mango.products.infrastructure.rest.mapper.PriceDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class PriceController implements PricesApi {

    private final AddPriceToProductUseCase addPriceToProductUseCase;
    private final GetProductPricesUseCase getProductPricesUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;

    public PriceController(AddPriceToProductUseCase addPriceToProductUseCase,
                          GetProductPricesUseCase getProductPricesUseCase,
                          GetProductByIdUseCase getProductByIdUseCase) {
        this.addPriceToProductUseCase = addPriceToProductUseCase;
        this.getProductPricesUseCase = getProductPricesUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    @Override
    public ResponseEntity<PriceResponse> addPriceToProduct(Long id, AddPriceRequest request) {
        Price price = addPriceToProductUseCase.execute(
            id,
            java.math.BigDecimal.valueOf(request.getValue()),
            request.getInitDate(),
            request.getEndDate()
        );
        PriceResponse response = PriceDtoMapper.toPriceResponse(price);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<GetProductPrices200Response> getProductPrices(Long id, LocalDate date) {
        if (date != null) {
            Optional<Price> priceOpt = getProductPricesUseCase.getCurrentPrice(id, date);
            Price price = priceOpt.orElseThrow(() -> new PriceNotFoundException(id, date));

            GetProductPrices200Response response = PriceDtoMapper.toCurrentPriceResponse(price);
            return ResponseEntity.ok(response);
        } else {
            Product product = getProductByIdUseCase.execute(id);
            List<Price> prices = getProductPricesUseCase.getAllPrices(id);

            GetProductPrices200Response response = PriceDtoMapper.toHistoryResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                prices
            );
            return ResponseEntity.ok(response);
        }
    }
}
