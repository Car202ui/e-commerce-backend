package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.dto.TopSoldProductDto;
import com.proyecto.backend.e_commerce.domain.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
           SELECT new com.proyecto.backend.e_commerce.dto.TopSoldProductDto(
             oi.product.id,
             oi.product.name,
             SUM(COALESCE(oi.quantity, 0))
           )
           FROM OrderItem oi
           WHERE oi.product IS NOT NULL
           GROUP BY oi.product.id, oi.product.name
           ORDER BY SUM(COALESCE(oi.quantity, 0)) DESC
           """)
    List<TopSoldProductDto> findTopBestSellingProducts(Pageable pageable);
}
