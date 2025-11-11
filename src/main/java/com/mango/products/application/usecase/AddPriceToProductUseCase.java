package com.mango.products.application.usecase;

import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.service.PriceOverlapValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public Price execute(Long productId, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Price newPrice = Price.create(product.getId(), value, initDate, endDate);

        List<Price> existingPrices = priceRepository.findByProductId(productId);
        overlapValidator.validate(newPrice, existingPrices);

        return priceRepository.save(newPrice);
    }
}