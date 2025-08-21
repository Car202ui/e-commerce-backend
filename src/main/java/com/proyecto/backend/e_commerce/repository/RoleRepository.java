package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role,Long> {

    Optional<Role> findByName(String name);
}
