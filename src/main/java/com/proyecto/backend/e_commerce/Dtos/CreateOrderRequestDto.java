package com.proyecto.backend.e_commerce.Dtos;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDto {

    private List<OrderItemRequestDto> items;
}
