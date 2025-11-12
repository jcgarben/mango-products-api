package com.mango.products.infrastructure.persistence.repository;

import com.mango.products.infrastructure.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPriceRepository extends JpaRepository<PriceEntity, Long> {

    List<PriceEntity> findByProductIdOrderByInitDateDesc(Long productId);

    List<PriceEntity> findByProductIdAndCurrencyOrderByInitDateDesc(Long productId, String currency);

    @Query("SELECT p FROM PriceEntity p WHERE p.productId = :productId " +
           "AND p.initDate <= :date " +
           "AND (p.endDate IS NULL OR p.endDate >= :date)")
    List<PriceEntity> findByProductIdAndDate(@Param("productId") Long productId,
                                              @Param("date") LocalDate date);

    @Query("SELECT p FROM PriceEntity p WHERE p.productId = :productId " +
           "AND p.currency = :currency " +
           "AND p.initDate <= :date " +
           "AND (p.endDate IS NULL OR p.endDate >= :date)")
    Optional<PriceEntity> findByProductIdAndCurrencyAndDate(@Param("productId") Long productId,
                                                              @Param("currency") String currency,
                                                              @Param("date") LocalDate date);
}
