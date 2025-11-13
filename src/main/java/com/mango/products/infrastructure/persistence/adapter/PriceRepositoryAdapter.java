package com.mango.products.infrastructure.persistence.adapter;

import com.mango.products.application.port.exception.RepositoryConstraintViolationException;
import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.domain.model.Price;
import com.mango.products.infrastructure.persistence.entity.PriceEntity;
import com.mango.products.infrastructure.persistence.mapper.PriceMapper;
import com.mango.products.infrastructure.persistence.repository.JpaPriceRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PriceRepositoryAdapter implements PriceRepository {

    private final JpaPriceRepository jpaRepository;

    public PriceRepositoryAdapter(JpaPriceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Price save(Price price) {
        try {
            PriceEntity entity = PriceMapper.toEntity(price);
            PriceEntity saved = jpaRepository.save(entity);
            return PriceMapper.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            throw new RepositoryConstraintViolationException("Data constraint violation while saving price", e);
        }
    }

    @Override
    public List<Price> findByProductId(Long productId) {
        return jpaRepository.findByProductIdOrderByInitDateDesc(productId)
            .stream()
            .map(PriceMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Price> findByProductIdAndCurrency(Long productId, String currencyCode) {
        return jpaRepository.findByProductIdAndCurrencyOrderByInitDateDesc(productId, currencyCode)
            .stream()
            .map(PriceMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Price> findByProductIdAndDate(Long productId, LocalDate date) {
        return jpaRepository.findByProductIdAndDate(productId, date)
            .stream()
            .map(PriceMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Price> findByProductIdAndCurrencyAndDate(Long productId, String currencyCode, LocalDate date) {
        return jpaRepository.findByProductIdAndCurrencyAndDate(productId, currencyCode, date)
            .map(PriceMapper::toDomain);
    }
}

