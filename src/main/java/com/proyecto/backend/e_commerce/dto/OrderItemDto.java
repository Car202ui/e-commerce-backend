package com.proyecto.backend.e_commerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long productId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private String productName;
}
