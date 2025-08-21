package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository  extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);
}
