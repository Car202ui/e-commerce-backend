package com.proyecto.backend.e_commerce.service.ImplService;

import com.proyecto.backend.e_commerce.dto.*;
import com.proyecto.backend.e_commerce.domain.*;
import com.proyecto.backend.e_commerce.exception.ResourceNotFoundException;
import com.proyecto.backend.e_commerce.repository.*;
import com.proyecto.backend.e_commerce.service.Iservice.IOrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl  implements IOrderService {


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${promotion.discount.enabled}")
    private boolean promotionEnabled;

    @Value("${promotion.discount.start-date}")
    private LocalDateTime promotionStartDate;

    @Value("${promotion.discount.end-date}")
    private LocalDateTime promotionEndDate;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequestDto orderRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Order order = new Order();
        order.setUser(currentUser);
        order.setStatus("COMPLETED");

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

        BigDecimal discountApplied = calculateDiscount(totalAmount, currentUser, orderRequest.isRandomOrder());
        BigDecimal finalAmount = totalAmount.subtract(discountApplied);

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setDiscountApplied(discountApplied);
        order.setFinalAmount(finalAmount);

        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }


    private BigDecimal calculateDiscount(BigDecimal totalAmount, User user, boolean isRandomOrder) {
        LocalDateTime now = LocalDateTime.now();

        if (isRandomOrder && promotionEnabled && now.isAfter(promotionStartDate) && now.isBefore(promotionEndDate)) {
            return totalAmount.multiply(new BigDecimal("0.50"));
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;

        if (promotionEnabled && now.isAfter(promotionStartDate) && now.isBefore(promotionEndDate)) {
            totalDiscount = totalDiscount.add(totalAmount.multiply(new BigDecimal("0.10")));
        }

        if (user.isFrequent()) {
            totalDiscount = totalDiscount.add(totalAmount.multiply(new BigDecimal("0.05")));
        }

        return totalDiscount;
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

    @Override
    public List<TopSoldProductDto> getTop5BestSellingProducts() {
        return orderItemRepository.findTop5BestSellingProducts();
    }

    @Override
    public List<TopCustomerDto> getTop5FrequentCustomers() {
        return orderRepository.findTop5FrequentCustomers();
    }


}
