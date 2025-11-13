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

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;

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
    public ResponseEntity<PriceResponse> addPriceToProduct(@PathVariable("id") Long id, @Valid @RequestBody AddPriceRequest request) {
        Price price = addPriceToProductUseCase.execute(
            id,
            java.math.BigDecimal.valueOf(request.getValue()),
            request.getCurrency(),
            request.getInitDate(),
            request.getEndDate()
        );
        PriceResponse response = PriceDtoMapper.toPriceResponse(price);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<GetProductPrices200Response> getProductPrices(@PathVariable("id") Long id,
                                                                        @PathVariable("date") LocalDate date,
                                                                        @PathVariable("currency")String currency) {
        if (date != null) {
            // Get current price(s) for specific date
            if (currency != null) {
                // Specific currency requested
                Optional<Price> priceOpt = getProductPricesUseCase.getCurrentPriceByCurrency(id, currency, date);
                Price price = priceOpt.orElseThrow(() -> new PriceNotFoundException(id, date));
                GetProductPrices200Response response = PriceDtoMapper.toCurrentPriceResponse(price);
                return ResponseEntity.ok(response);
            } else {
                // No currency specified - return all prices for that date
                List<Price> prices = getProductPricesUseCase.getCurrentPrices(id, date);
                if (prices.isEmpty()) {
                    throw new PriceNotFoundException(id, date);
                }
                // If only one price, return as current price
                if (prices.size() == 1) {
                    GetProductPrices200Response response = PriceDtoMapper.toCurrentPriceResponse(prices.get(0));
                    return ResponseEntity.ok(response);
                }
                // Multiple prices (different currencies) - return as list
                Product product = getProductByIdUseCase.execute(id);
                GetProductPrices200Response response = PriceDtoMapper.toHistoryResponse(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    prices
                );
                return ResponseEntity.ok(response);
            }
        } else {
            // Get price history
            Product product = getProductByIdUseCase.execute(id);
            List<Price> prices;
            if (currency != null) {
                prices = getProductPricesUseCase.getAllPricesByCurrency(id, currency);
            } else {
                prices = getProductPricesUseCase.getAllPrices(id);
            }

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
