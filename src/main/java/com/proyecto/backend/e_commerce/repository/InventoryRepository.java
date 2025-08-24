package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository  extends JpaRepository<Inventory, Long> {


    Optional<Inventory> findByProduct_Id(Long productId);
    List<Inventory> findByProduct_IdIn(Collection<Long> productIds);

    Optional<Inventory> findByProductId(Long productId);
    List<Inventory> findByProductIdIn(Collection<Long> productIds);
}
