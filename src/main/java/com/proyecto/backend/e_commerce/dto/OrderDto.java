package com.proyecto.backend.e_commerce.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {

    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountApplied;
    private BigDecimal finalAmount;
    private List<OrderItemDto> items;
}
