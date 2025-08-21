package com.proyecto.backend.e_commerce.dto;

import lombok.Data;

@Data
public class InventoryDto {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
}
