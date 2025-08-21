package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.dto.TopSoldProductDto;
import com.proyecto.backend.e_commerce.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT new com.proyecto.backend.e_commerce.dto.TopSoldProductDto(oi.product.id, oi.product.name, SUM(oi.quantity)) " + // <-- CORREGIDO A 'Dtos'
            "FROM OrderItem oi " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY SUM(oi.quantity) DESC " +
            "LIMIT 5")
    List<TopSoldProductDto> findTop5BestSellingProducts();
}
