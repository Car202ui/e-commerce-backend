package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  OrderRepository  extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
