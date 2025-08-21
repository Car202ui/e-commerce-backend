package com.proyecto.backend.e_commerce.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopCustomerDto {
    private Long userId;
    private String username;
    private Long orderCount;
}
