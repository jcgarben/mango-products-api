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

    @Query("SELECT p FROM PriceEntity p WHERE p.productId = :productId " +
           "AND p.initDate <= :date " +
           "AND (p.endDate IS NULL OR p.endDate >= :date)")
    Optional<PriceEntity> findByProductIdAndDate(@Param("productId") Long productId,
                                                   @Param("date") LocalDate date);
}
