package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
}
