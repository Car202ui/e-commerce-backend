package com.proyecto.backend.e_commerce.controller;


import com.proyecto.backend.e_commerce.dto.InventoryDto;
import com.proyecto.backend.e_commerce.service.Iservice.IInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Gestión de Inventario", description = "API para consultar y actualizar el stock de los productos.")
@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final IInventoryService inventoryService;

    public InventoryController(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Establecer o crear el inventario para un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario establecido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PostMapping("/{productId}")
    public ResponseEntity<InventoryDto> setInventory(
            @Parameter(description = "ID del producto al que se le asignará el inventario") @PathVariable Long productId,
            @Parameter(description = "Cantidad inicial de stock") @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.setInventory(productId, quantity));
    }

    @Operation(summary = "Actualizar el inventario de un producto (añadir o restar stock)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "La cantidad resultante es negativa"),
            @ApiResponse(responseCode = "404", description = "Producto o inventario no encontrado")
    })
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryDto> updateInventory(
            @Parameter(description = "ID del producto cuyo inventario se actualizará") @PathVariable Long productId,
            @Parameter(description = "Cantidad a añadir (positivo) o restar (negativo)") @RequestParam Integer quantityChange) {
        return ResponseEntity.ok(inventoryService.updateInventory(productId, quantityChange));
    }

    @Operation(summary = "Consultar el inventario de un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto o inventario no encontrado")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDto> getInventoryByProductId(
            @Parameter(description = "ID del producto cuyo inventario se desea consultar") @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }
}
