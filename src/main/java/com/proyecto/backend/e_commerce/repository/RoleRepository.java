package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository  extends JpaRepository<Role,Long> {
    Set<Role> findByNameIn(Collection<String> names);
    Optional<Role> findByName(String name);
}
