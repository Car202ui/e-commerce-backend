package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
