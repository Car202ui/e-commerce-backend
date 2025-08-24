package com.proyecto.backend.e_commerce.service.Iservice;


import com.proyecto.backend.e_commerce.dto.RegisterDto;
import com.proyecto.backend.e_commerce.dto.UserDto;
import com.proyecto.backend.e_commerce.domain.User;
import com.proyecto.backend.e_commerce.dto.UserUpdateRequestDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IUserService {
    User registerUser(RegisterDto registerDto);
    String generateToken(Authentication authentication);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long userId);
    void deleteUser(Long id);
    UserDto updateUser(Long id, UserUpdateRequestDto request);
}
