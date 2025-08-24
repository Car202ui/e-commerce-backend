package com.proyecto.backend.e_commerce.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateRequestDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean frequent;
    private Set<String> roles;
}
