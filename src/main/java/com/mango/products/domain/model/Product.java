package com.mango.products.domain.model;

import java.util.Objects;

public class Product {

    private Long id;
    private String name;
    private String description;

    private Product(Long id, String name, String description) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Product name cannot be null");
        this.description = description;
    }

    public static Product create(String name, String description) {
        return new Product(null, name, description);
    }

    public static Product of(Long id, String name, String description) {
        return new Product(id, name, description);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        return Objects.equals(id, ((Product) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', description='" + description + "'}";
    }
}