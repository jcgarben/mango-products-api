package com.mango.products.infrastructure.persistence.adapter;

import com.mango.products.application.port.out.PriceRepository;
import com.mango.products.domain.exception.PriceOverlapException;
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
            throw new PriceOverlapException(
                price.getProductId(),
                price.getInitDate(),
                price.getEndDate()
            );
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
    public Optional<Price> findByProductIdAndDate(Long productId, LocalDate date) {
        return jpaRepository.findByProductIdAndDate(productId, date)
            .map(PriceMapper::toDomain);
    }
}

