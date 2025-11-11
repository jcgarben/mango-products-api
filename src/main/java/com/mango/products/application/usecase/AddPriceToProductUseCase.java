package com.mango.products.application.usecase;

import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class AddPriceToProductUseCase {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    public AddPriceToProductUseCase(ProductRepository productRepository, PriceRepository priceRepository) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
    }

    public Price execute(Long productId, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        Price price = Price.create(product.getId(), value, initDate, endDate);
        return priceRepository.save(price);
    }
}