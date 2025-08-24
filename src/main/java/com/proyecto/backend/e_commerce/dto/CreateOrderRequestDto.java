package com.proyecto.backend.e_commerce.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDto {


    @NotEmpty
    private List<OrderItemRequestDto> items;

    @JsonProperty("randomOrder")
    @JsonAlias({"isRandomOrder"})
    private boolean isRandomOrder;
}
