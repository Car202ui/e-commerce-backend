package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.dto.TopCustomerDto;
import com.proyecto.backend.e_commerce.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface  OrderRepository  extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query("SELECT new com.proyecto.backend.e_commerce.dto.TopCustomerDto(o.user.id, o.user.username, COUNT(o.id)) " +
            "FROM Order o " +
            "GROUP BY o.user.id, o.user.username " +
            "ORDER BY COUNT(o.id) DESC")
    List<TopCustomerDto> findTopFrequentCustomers(Pageable pageable);
}
