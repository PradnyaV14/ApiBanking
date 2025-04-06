
package com.productManagement.repository;

import com.productManagement.model.Product;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository class
 */
@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
}

