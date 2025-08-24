package com.proyecto.backend.e_commerce.controller;

import com.proyecto.backend.e_commerce.dto.CreateOrderRequestDto;
import com.proyecto.backend.e_commerce.dto.OrderDto;
import com.proyecto.backend.e_commerce.service.Iservice.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestión de Órdenes", description = "API para crear y consultar las órdenes de compra.")
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Crear una nueva orden de compra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Petición inválida (ej. stock insuficiente)"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado")
    })
    @PostMapping("/create")
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderRequestDto orderRequest) {
        OrderDto createdOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una orden específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(
            @Parameter(description = "ID de la orden que se desea obtener") @PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @Operation(summary = "Obtener el historial de órdenes del usuario actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de órdenes devuelto exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto>> getMyOrders() {
        return ResponseEntity.ok(orderService.getOrdersForCurrentUser());
    }
}
