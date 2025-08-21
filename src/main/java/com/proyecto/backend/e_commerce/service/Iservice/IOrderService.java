package com.proyecto.backend.e_commerce.service.Iservice;

import com.proyecto.backend.e_commerce.dto.CreateOrderRequestDto;
import com.proyecto.backend.e_commerce.dto.OrderDto;
import com.proyecto.backend.e_commerce.dto.TopCustomerDto;
import com.proyecto.backend.e_commerce.dto.TopSoldProductDto;

import java.util.List;

public interface IOrderService {

    OrderDto createOrder(CreateOrderRequestDto orderRequest);
    OrderDto getOrderById(Long orderId);
    List<OrderDto> getOrdersForCurrentUser();
    List<TopSoldProductDto> getTop5BestSellingProducts();
    List<TopCustomerDto> getTop5FrequentCustomers();
}
