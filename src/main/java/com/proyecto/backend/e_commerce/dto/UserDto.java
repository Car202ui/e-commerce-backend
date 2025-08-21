package com.proyecto.backend.e_commerce.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isFrequent;
    private Set<String> roles;
}
