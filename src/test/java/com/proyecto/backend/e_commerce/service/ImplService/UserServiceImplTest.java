package com.proyecto.backend.e_commerce.service.ImplService;

import com.proyecto.backend.e_commerce.domain.Role;
import com.proyecto.backend.e_commerce.domain.User;
import com.proyecto.backend.e_commerce.dto.RegisterDto;
import com.proyecto.backend.e_commerce.exception.ResourceAlreadyExistsException;
import com.proyecto.backend.e_commerce.repository.RoleRepository;
import com.proyecto.backend.e_commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterDto registerDto;
    private Role userRole;

    @BeforeEach
    void setUp() {
        registerDto = new RegisterDto();
        registerDto.setUsername("nuevoUsuario");
        registerDto.setEmail("nuevo@correo.com");
        registerDto.setPassword("password123");

        userRole = new Role(1L, "ROLE_USER");
    }

    @DisplayName("Prueba para registrar un nuevo usuario exitosamente")
    @Test
    void whenRegisterUser_withNewUsername_thenUserIsSaved() {
        given(userRepository.findByUsername(registerDto.getUsername())).willReturn(Optional.empty());
        given(passwordEncoder.encode(registerDto.getPassword())).willReturn("passwordHasheado");
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(userRole));
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(registerDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("nuevoUsuario");
        assertThat(savedUser.getPassword()).isEqualTo("passwordHasheado");
    }

    @DisplayName("Prueba para fallar registro con un username que ya existe")
    @Test
    void whenRegisterUser_withExistingUsername_thenThrowException() {
        given(userRepository.findByUsername(registerDto.getUsername())).willReturn(Optional.of(new User()));
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.registerUser(registerDto);
        });
    }
}
