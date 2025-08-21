package com.proyecto.backend.e_commerce.sevice.Iservice;

import com.proyecto.backend.e_commerce.Dtos.InventoryDto;

public interface IInventoryService {
    InventoryDto setInventory(Long productId, Integer quantity);
    InventoryDto updateInventory(Long productId, Integer quantityChange);
    InventoryDto getInventoryByProductId(Long productId);
}
