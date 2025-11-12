package com.mango.products.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Price {

    private Long id;
    private Long productId;
    private BigDecimal value;
    private LocalDate initDate;
    private LocalDate endDate;

    private Price(Long id, Long productId, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this.id = id;
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.value = Objects.requireNonNull(value, "Price value cannot be null");
        this.initDate = Objects.requireNonNull(initDate, "Init date cannot be null");
        this.endDate = endDate;

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price value must be greater than zero");
        }

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

    /**
     * Determines if this price overlaps with another price.
     * Two prices overlap if they share at least one day in common and belong to the same product.
     *
     * @param other the other price to compare with
     * @return true if the prices overlap, false otherwise
     */
    public boolean overlaps(Price other) {
        // No overlap if the other price is null or belongs to a different product
        if (other == null || !this.productId.equals(other.productId)) {
            return false;
        }

        // If both prices are open-ended (no end date), they always overlap
        // because both extend indefinitely into the future
        if (this.endDate == null && other.endDate == null) {
            return true;
        }

        // If this price has no end date (open-ended), it overlaps with any price
        // that starts on or after this price's start date
        if (this.endDate == null) {
            return !other.initDate.isBefore(this.initDate);
        }

        // If the other price has no end date (open-ended), it overlaps if
        // this price ends on or after the other price's start date
        if (other.endDate == null) {
            return !this.endDate.isBefore(other.initDate);
        }

        // Both prices have end dates - they overlap if one doesn't end before the other starts
        // They do NOT overlap only if: this ends before other starts OR other ends before this starts
        return !(this.endDate.isBefore(other.initDate) || other.endDate.isBefore(this.initDate));
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

