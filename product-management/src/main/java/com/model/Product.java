package com.productManagement.model;

import java.util.List;
import java.util.UUID;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;

/**
 * Product entity class
 */
@Entity
@Cacheable
@Table(name = "PRODUCT")
@SequenceGenerator(name = "productSeq", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
public class Product extends PanacheEntity{
	
    @Column(name = "NAME", length = 40, nullable = false)
    public String name;

    @Column(name = "DESCRIPTION", length = 255)
    public String description;

    @Column(name = "PRICE", nullable = false)
    public Double price;

    @Column(name = "QUANTITY", nullable = false)
    public Integer quantity;

    public static Uni<List<Product>> getAllProducts() {
        return Product.listAll();
    }

    public static Uni<Product> findByIdReactive(Long id) {
        return Product.findById(id);
    }

    public static Uni<Boolean> deleteProduct(Long id) {
        return Product.deleteById(id);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

}
