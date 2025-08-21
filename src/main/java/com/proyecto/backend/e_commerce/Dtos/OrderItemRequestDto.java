package com.proyecto.backend.e_commerce.Dtos;


import lombok.Data;

@Data
public class OrderItemRequestDto {
    private Long productId;
    private Integer quantity;
}
