package com.mango.products.domain.service;

import com.mango.products.domain.exception.PriceOverlapException;
import com.mango.products.domain.model.Price;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PriceOverlapValidator {

    public void validate(Price newPrice, List<Price> existingPrices) {
        if (existingPrices == null || existingPrices.isEmpty()) {
            return;
        }

        for (Price existingPrice : existingPrices) {
            if (newPrice.overlaps(existingPrice)) {
                throw new PriceOverlapException(
                        newPrice.getProductId(),
                        newPrice.getInitDate(),
                        newPrice.getEndDate()
                );
            }
        }
    }
}

