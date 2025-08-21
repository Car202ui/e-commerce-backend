package com.proyecto.backend.e_commerce.Dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long productId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private String productName;
}
