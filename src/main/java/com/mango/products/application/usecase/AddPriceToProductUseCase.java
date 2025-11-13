package com.mango.products.application.usecase;

import com.mango.products.application.port.exception.RepositoryConstraintViolationException;
import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.InvalidCurrencyException;
import com.mango.products.domain.exception.PriceOverlapException;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.service.PriceOverlapValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

@Service
@Transactional
public class AddPriceToProductUseCase {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final PriceOverlapValidator overlapValidator;

    public AddPriceToProductUseCase(
            ProductRepository productRepository,
            PriceRepository priceRepository,
            PriceOverlapValidator overlapValidator) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.overlapValidator = overlapValidator;
    }

    public Price execute(Long productId, BigDecimal value, String currencyCode, LocalDate initDate, LocalDate endDate) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // Validate currency code using Java's Currency API
        Currency currency;
        try {
            currency = java.util.Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new InvalidCurrencyException(currencyCode);
        }

        Price newPrice = Price.create(product.getId(), value, currency, initDate, endDate);

        // Validate overlap only for prices with the same currency
        List<Price> existingPrices = priceRepository.findByProductIdAndCurrency(productId, currencyCode);
        overlapValidator.validate(newPrice, existingPrices);

        try {
            return priceRepository.save(newPrice);
        } catch (RepositoryConstraintViolationException e) {
            throw new PriceOverlapException(productId, initDate, endDate);
        }
    }
}