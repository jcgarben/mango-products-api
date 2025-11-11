package com.mango.products.infrastructure.rest.mapper;

import com.mango.products.domain.model.Price;
import com.mango.products.infrastructure.rest.dto.CurrentPriceResponse;
import com.mango.products.infrastructure.rest.dto.PriceResponse;
import com.mango.products.infrastructure.rest.dto.ProductPriceHistoryResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PriceDtoMapper {


    public static PriceResponse toPriceResponse(Price price) {
        PriceResponse response = new PriceResponse();
        response.setId(price.getId());
        response.setValue(price.getValue().doubleValue());
        response.setInitDate(price.getInitDate());
        response.setEndDate(price.getEndDate());
        return response;
    }

    public static CurrentPriceResponse toCurrentPriceResponse(Price price) {
        CurrentPriceResponse response = new CurrentPriceResponse();
        response.setValue(price.getValue().doubleValue());
        return response;
    }

    public static ProductPriceHistoryResponse toHistoryResponse(Long productId, String name, String description, List<Price> prices) {
        ProductPriceHistoryResponse response = new ProductPriceHistoryResponse();
        response.setId(productId);
        response.setName(name);
        response.setDescription(description);
        response.setPrices(prices.stream()
            .map(PriceDtoMapper::toPriceResponse)
            .collect(Collectors.toList()));
        return response;
    }
}


