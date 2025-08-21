package com.proyecto.backend.e_commerce.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSoldProductDto {
    private Long productId;
    private String productName;
    private Long totalSold;
}
