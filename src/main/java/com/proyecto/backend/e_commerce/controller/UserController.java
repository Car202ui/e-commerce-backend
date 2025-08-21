package com.proyecto.backend.e_commerce.controller;

import com.proyecto.backend.e_commerce.dto.UserDto;
import com.proyecto.backend.e_commerce.service.Iservice.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestión de Usuarios (Admin)", description = "API para que los administradores gestionen a los usuarios.")
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtener la lista de todos los usuarios (Solo Admin)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Obtener un usuario por su ID (Solo Admin)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
