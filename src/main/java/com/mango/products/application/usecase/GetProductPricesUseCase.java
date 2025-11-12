package com.mango.products.application.usecase;

import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.application.port.out.ProductRepository;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GetProductPricesUseCase {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    public GetProductPricesUseCase(ProductRepository productRepository, PriceRepository priceRepository) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
    }

    public List<Price> getAllPrices(Long productId) {
        verifyProductExists(productId);
        return priceRepository.findByProductId(productId);
    }

    public List<Price> getAllPricesByCurrency(Long productId, String currencyCode) {
        verifyProductExists(productId);
        return priceRepository.findByProductIdAndCurrency(productId, currencyCode);
    }

    public List<Price> getCurrentPrices(Long productId, LocalDate date) {
        verifyProductExists(productId);
        return priceRepository.findByProductIdAndDate(productId, date);
    }

    public Optional<Price> getCurrentPriceByCurrency(Long productId, String currencyCode, LocalDate date) {
        verifyProductExists(productId);
        return priceRepository.findByProductIdAndCurrencyAndDate(productId, currencyCode, date);
    }

    private void verifyProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
    }
}