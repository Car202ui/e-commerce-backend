package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue();
    List<Product> findByNameContainingIgnoreCase(String name);
}
