package com.proyecto.backend.e_commerce.Dtos;

import lombok.Data;

@Data
public class InventoryDto {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
}
