package com.example.funkos.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class FunkoUpdateDto {

    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String nombre;
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;
    @Min(value = 0, message = "La cantidad no puede ser menor a 0")
    private Integer cantidad;
    private String imagen;
    private String categoria;
    private final Boolean activo;

}
