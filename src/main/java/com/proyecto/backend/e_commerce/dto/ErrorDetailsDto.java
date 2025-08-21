package com.proyecto.backend.e_commerce.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorDetailsDto {

    private Date timestamp;
    private String message;
    private String details;

    public ErrorDetailsDto(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
