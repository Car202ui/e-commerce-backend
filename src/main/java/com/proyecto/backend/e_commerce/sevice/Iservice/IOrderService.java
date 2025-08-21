package com.proyecto.backend.e_commerce.sevice.Iservice;

import com.proyecto.backend.e_commerce.Dtos.CreateOrderRequestDto;
import com.proyecto.backend.e_commerce.Dtos.OrderDto;

import java.util.List;

public interface IOrderService {

    OrderDto createOrder(CreateOrderRequestDto orderRequest);
    OrderDto getOrderById(Long orderId);
    List<OrderDto> getOrdersForCurrentUser();
}
