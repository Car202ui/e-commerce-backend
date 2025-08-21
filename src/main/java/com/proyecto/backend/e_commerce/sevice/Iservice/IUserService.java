package com.proyecto.backend.e_commerce.sevice.Iservice;


import com.proyecto.backend.e_commerce.Dtos.LoginDto;
import com.proyecto.backend.e_commerce.Dtos.RegisterDto;
import com.proyecto.backend.e_commerce.domain.User;
import org.springframework.security.core.Authentication;

public interface IUserService {
    User registerUser(RegisterDto registerDto);
    String generateToken(Authentication authentication);
}
