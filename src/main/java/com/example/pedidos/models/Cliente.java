package com.example.pedidos.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record Cliente(
        @NotBlank(message = "El nombre no puede estar vacio")
        @Length(min = 6, message = "El nombre debe tener al menos 6 caracteres")
        String nombreCompleto,
        @Email(message = "El email debe ser valido")
        String email,
        @NotBlank(message = "El telefono no puede estar vacio")
        String telefono,
        @NotNull(message = "La direccion no puede ser nula")
        @Valid Direccion direccion
) {

}
