package com.example.categoria.dto;

import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CategoriaDto {
    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private final String nombre;
    @Column(columnDefinition = "boolean default true")
    private final Boolean activo;
}

