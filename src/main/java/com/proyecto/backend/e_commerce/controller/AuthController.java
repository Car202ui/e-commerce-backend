package com.proyecto.backend.e_commerce.controller;


import com.proyecto.backend.e_commerce.Dtos.JwtAuthResponseDto;
import com.proyecto.backend.e_commerce.Dtos.LoginDto;
import com.proyecto.backend.e_commerce.Dtos.RegisterDto;
import com.proyecto.backend.e_commerce.sevice.Iservice.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "API para el registro y login de usuarios.")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final IUserService  userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(IUserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Registrar un nuevo usuario en el sistema")
    @ApiResponses(value = { // Anotación para documentar los códigos de respuesta
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos de entrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor (ej. el usuario o email ya existe)")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        return new ResponseEntity<>("Usuario registrado exitosamente!", HttpStatus.CREATED);
    }


    @Operation(summary = "Iniciar sesión para obtener un token de autenticación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, token devuelto"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> login(@Valid @RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = userService.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthResponseDto(token));
    }

}
