package com.mango.products.domain.exception;

import java.time.LocalDate;

public class PriceOverlapException extends RuntimeException {

    public PriceOverlapException(Long productId, LocalDate initDate, LocalDate endDate) {
        super("A price already exists for product " + productId +
                " in the date range [" + initDate + ", " + (endDate != null ? endDate : "infinity") + "]");
    }
}