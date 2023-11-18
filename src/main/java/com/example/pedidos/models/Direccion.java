package com.example.pedidos.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record Direccion(
        @Length(min = 4, message = "La calle debe tener al menos 4 caracteres")
        String calle,
        @NotBlank(message = "El numero no puede estar vacio")
        String numero,
        @Length(min = 3, message = "La ciudad debe tener al menos 3 caracteres")
        String ciudad,
        @Length(min = 3, message = "La provincia debe tener al menos 3 caracteres")
        String provincia,
        @NotBlank(message = "El codigo postal no puede estar vacio")
        @Pattern(regexp = "^[0-9]{5}$", message = "El codigo postal debe tener 5 digitos")
        String codigoPostal
) {
}
