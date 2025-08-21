package com.proyecto.backend.e_commerce.sevice.ImplService;

import com.proyecto.backend.e_commerce.Dtos.CreateOrderRequestDto;
import com.proyecto.backend.e_commerce.Dtos.OrderDto;
import com.proyecto.backend.e_commerce.Dtos.OrderItemDto;
import com.proyecto.backend.e_commerce.Dtos.OrderItemRequestDto;
import com.proyecto.backend.e_commerce.domain.*;
import com.proyecto.backend.e_commerce.exception.ResourceNotFoundException;
import com.proyecto.backend.e_commerce.repository.InventoryRepository;
import com.proyecto.backend.e_commerce.repository.OrderRepository;
import com.proyecto.backend.e_commerce.repository.ProductRepository;
import com.proyecto.backend.e_commerce.repository.UserRepository;
import com.proyecto.backend.e_commerce.sevice.Iservice.IOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl  implements IOrderService {


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequestDto orderRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Order order = new Order();
        order.setUser(currentUser);
        order.setStatus("PENDING");

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;


        for (OrderItemRequestDto itemDto : orderRequest.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + itemDto.getProductId()));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para el producto: " + product.getName()));


            if (inventory.getQuantity() < itemDto.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getName());
            }


            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPricePerUnit(product.getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);


            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));


            inventory.setQuantity(inventory.getQuantity() - itemDto.getQuantity());
            inventoryRepository.save(inventory);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);


        order.setFinalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return mapToDto(savedOrder);
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + orderId));
        return mapToDto(order);
    }

    @Override
    public List<OrderDto> getOrdersForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        return orderRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setUsername(order.getUser().getUsername());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountApplied(order.getDiscountApplied());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setItems(order.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDto mapItemToDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPricePerUnit(item.getPricePerUnit());
        return dto;
    }
}
