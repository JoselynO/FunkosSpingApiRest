package com.example.funkos.dto;

import com.example.funkos.models.Funko;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@Schema(description = "Funko a actualizar")
public class FunkoUpdateDto {
    @Schema(description = "Nombre del funko", example = "Harry Potter")
    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String nombre;
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio del funko", example = "89.99")
    private Double precio;
    @Min(value = 0, message = "La cantidad no puede ser menor a 0")
    @Schema(description = "Cantidad del funko", example = "5")
    private Integer cantidad;
    @Schema(description = "Imagen del funko", example = Funko.IMAGE_DEFAULT)
    private String imagen;
    @Schema(description = "Categoria del funko", example = "DISNEY")
    private String categoria;
    @Schema(description = "Si el funko esta activo", example = "true")
    private final Boolean activo;
    @JsonCreator
    public FunkoUpdateDto(
            @JsonProperty("nombre") String nombre,
            @JsonProperty("precio") double precio,
            @JsonProperty("cantidad") int cantidad,
            @JsonProperty("imagen") String imagen,
            @JsonProperty("categoria") String categoria,
            @JsonProperty("activo") Boolean activo
    ) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagen = imagen;
        this.categoria = categoria;
        this.activo = activo;
    }
}

