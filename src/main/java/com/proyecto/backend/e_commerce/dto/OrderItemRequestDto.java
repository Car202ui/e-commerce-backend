package com.proyecto.backend.e_commerce.dto;


import lombok.Data;

@Data
public class OrderItemRequestDto {
    private Long productId;
    private Integer quantity;
}
