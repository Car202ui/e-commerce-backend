package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository  extends JpaRepository<Role,Long> {
}
