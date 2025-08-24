package com.proyecto.backend.e_commerce.service.ImplService;

import com.proyecto.backend.e_commerce.dto.InventoryDto;
import com.proyecto.backend.e_commerce.domain.Inventory;
import com.proyecto.backend.e_commerce.domain.Product;
import com.proyecto.backend.e_commerce.exception.ResourceNotFoundException;
import com.proyecto.backend.e_commerce.repository.InventoryRepository;
import com.proyecto.backend.e_commerce.repository.ProductRepository;
import com.proyecto.backend.e_commerce.service.Iservice.IInventoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements IInventoryService {

  private  final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }


    private InventoryDto mapToDto(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProduct().getId());
        dto.setProductName(inventory.getProduct().getName());
        dto.setQuantity(inventory.getQuantity());
        return dto;
    }

    @Override
    @Transactional
    public InventoryDto setInventory(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productId));

        Inventory inventory = inventoryRepository.findByProduct_Id(productId)
                .orElse(new Inventory());

        inventory.setProduct(product);
        inventory.setQuantity(quantity);

        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToDto(savedInventory);
    }

    @Override
    @Transactional
    public InventoryDto updateInventory(Long productId, Integer quantityChange) {
        Inventory inventory = inventoryRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para el producto con id: " + productId));

        int newQuantity = inventory.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("La cantidad en el inventario no puede ser negativa.");
        }
        inventory.setQuantity(newQuantity);

        Inventory updatedInventory = inventoryRepository.save(inventory);
        return mapToDto(updatedInventory);
    }

    @Override
    public InventoryDto getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para el producto con id: " + productId));
        return mapToDto(inventory);
    }
}
