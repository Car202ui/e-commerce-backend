package com.proyecto.backend.e_commerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDto {

    private List<OrderItemRequestDto> items;
    private boolean isRandomOrder;
}
