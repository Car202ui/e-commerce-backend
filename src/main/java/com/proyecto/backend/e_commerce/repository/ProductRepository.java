package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
      SELECT p.*
      FROM products p
      WHERE (:q IS NULL OR
             LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:q AS TEXT), '%')) OR
             LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', CAST(:q AS TEXT), '%'))
      )
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
      AND (:active IS NULL OR p.is_active = :active)
      AND (:id IS NULL OR p.id = :id)
      ORDER BY p.id DESC
      """, nativeQuery = true)
    List<Product> search(
            @Param("q") String q,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("active") Boolean active,
            @Param("id") Long id
    );

    List<Product> findByIsActiveTrue();
    List<Product> findByNameContainingIgnoreCase(String name);
}
