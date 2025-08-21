package com.proyecto.backend.e_commerce.controller;


import com.proyecto.backend.e_commerce.dto.ProductDto;
import com.proyecto.backend.e_commerce.dto.TopCustomerDto;
import com.proyecto.backend.e_commerce.dto.TopSoldProductDto;
import com.proyecto.backend.e_commerce.service.Iservice.IOrderService;
import com.proyecto.backend.e_commerce.service.Iservice.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reportes", description = "API para generar reportes y obtener estadísticas.")
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final IProductService productService;
    private final IOrderService orderService;

    public ReportController(IProductService productService, IOrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @Operation(summary = "Obtener un reporte de todos los productos activos (Solo Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere rol de Administrador.")
    })
    @GetMapping("/products/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDto>> getActiveProductsReport() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    @Operation(summary = "Obtener un reporte del top 5 de productos más vendidos (Solo Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere rol de Administrador.")
    })
    @GetMapping("/products/top-selling")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TopSoldProductDto>> getTopSellingProductsReport() {
        return ResponseEntity.ok(orderService.getTop5BestSellingProducts());
    }


    @Operation(summary = "Obtener un reporte del top 5 de clientes más frecuentes (Solo Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere rol de Administrador.")
    })
    @GetMapping("/customers/top-frequent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TopCustomerDto>> getTopFrequentCustomersReport() {
        return ResponseEntity.ok(orderService.getTop5FrequentCustomers());
    }

    @Operation(summary = "Buscar productos por nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProductsByName(
            @Parameter(description = "Término de búsqueda para el nombre del producto") @RequestParam("name") String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

}
