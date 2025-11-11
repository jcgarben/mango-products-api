package com.mango.products.domain.model;

import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Price {

    @Setter
    private Long id;
    private Long productId;
    private BigDecimal value;
    private LocalDate initDate;
    private LocalDate endDate;

    protected Price() {
    }

    private Price(Long id, Long productId, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this.id = id;
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.value = Objects.requireNonNull(value, "Price value cannot be null");
        this.initDate = Objects.requireNonNull(initDate, "Init date cannot be null");
        this.endDate = endDate;

        if (endDate != null && endDate.isBefore(initDate)) {
            throw new IllegalArgumentException("End date must be after or equal to init date");
        }
    }

    public static Price create(Long productId, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        return new Price(null, productId, value, initDate, endDate);
    }

    public static Price of(Long id, Long productId, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        return new Price(id, productId, value, initDate, endDate);
    }

    public boolean isActiveOn(LocalDate date) {
        return !date.isBefore(initDate) && (endDate == null || !date.isAfter(endDate));
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price)) return false;
        return Objects.equals(id, ((Price) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Price{id=" + id + ", productId=" + productId + ", value=" + value + ", initDate=" + initDate +
                ", endDate=" + endDate + "}";
    }
}

