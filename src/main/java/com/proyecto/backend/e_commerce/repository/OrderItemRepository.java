package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
