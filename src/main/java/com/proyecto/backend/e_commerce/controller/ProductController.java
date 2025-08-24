package com.proyecto.backend.e_commerce.controller;


import com.proyecto.backend.e_commerce.domain.Inventory;
import com.proyecto.backend.e_commerce.dto.ProductDto;
import com.proyecto.backend.e_commerce.repository.InventoryRepository;
import com.proyecto.backend.e_commerce.repository.ProductRepository;
import com.proyecto.backend.e_commerce.service.Iservice.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Gestión de Productos", description = "API para realizar todas las operaciones (CRUD) sobre los productos.")
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final IProductService productService;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductController(IProductService productService, ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado para realizar esta acción")
    })
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        ProductDto createdProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "ID del producto que se desea obtener") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Obtener la lista de todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos devuelta exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Actualizar un producto existente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "ID del producto que se desea actualizar") @PathVariable Long id,
            @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @Operation(summary = "Eliminar un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @Parameter(description = "ID del producto que se desea eliminar") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Producto eliminado exitosamente.");
    }


    @Operation(
            summary = "Búsqueda avanzada de productos",
            description = "Filtra por texto (nombre/descr.), rango de precio, estado activo y/o ID exacto. "
                    + "Todos los parámetros son opcionales y combinables."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de productos",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/search")
    @Transactional(readOnly = true)
    public List<ProductDto> search(
            @Parameter(description = "Texto a buscar en nombre o descripción (contiene)", example = "laptop")
            @RequestParam(required = false) String q,

            @Parameter(description = "Precio mínimo", schema = @Schema(type = "number", example = "1000.00"))
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Precio máximo", schema = @Schema(type = "number", example = "2500.00"))
            @RequestParam(required = false) BigDecimal maxPrice,

            @Parameter(description = "Solo activos si true; solo inactivos si false; todos si se omite", example = "true")
            @RequestParam(required = false) Boolean active,

            @Parameter(description = "ID exacto del producto", example = "42")
            @RequestParam(required = false) Long id
    ) {
        return productRepository.search(q, minPrice, maxPrice, active, id)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ProductDto mapToDto(com.proyecto.backend.e_commerce.domain.Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setActive(p.isActive());
        Optional<Inventory> invOpt = inventoryRepository.findByProduct_Id(p.getId());
        dto.setStock(invOpt.map(Inventory::getQuantity).orElse(0));
        return dto;
    }
}
