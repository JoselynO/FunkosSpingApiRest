package com.example.funkos.dto;

import com.example.funkos.models.Funko;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@Schema(description = "Funko a crear")
public class FunkoCreateDto {
    @Schema(description = "Nombre del funko", example = "Harry Potter")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String nombre;
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio del funko", example = "89.99")
    private double precio;
    @Min(value = 0, message = "La cantidad no puede ser menor a 0")
    @Schema(description = "Cantidad del funko", example = "5")
    private int cantidad;
    @Schema(description = "Imagen del funko", example = Funko.IMAGE_DEFAULT)
    private String imagen;
    @Schema(description = "Categoria del funko", example = "DISNEY")
    @NotBlank(message = "La categoria no puede estar vacia")
    private String categoria;

}
