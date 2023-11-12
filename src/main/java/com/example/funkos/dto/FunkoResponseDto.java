package com.example.funkos.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Data
public class FunkoResponseDto {
    private Long id;
    private String nombre;
    private double precio;
    private int cantidad;
    private String imagen;
    private String categoria;
    private LocalDateTime fechaDeCreacion;
    private LocalDateTime fechaDeActualizacion;
}
