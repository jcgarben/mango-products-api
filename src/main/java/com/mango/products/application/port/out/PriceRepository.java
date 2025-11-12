package com.mango.products.application.port.out;

import com.mango.products.domain.model.Price;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceRepository {

    Price save(Price price);

    List<Price> findByProductId(Long productId);

    List<Price> findByProductIdAndCurrency(Long productId, String currencyCode);

    List<Price> findByProductIdAndDate(Long productId, LocalDate date);

    Optional<Price> findByProductIdAndCurrencyAndDate(Long productId, String currencyCode, LocalDate date);
}

