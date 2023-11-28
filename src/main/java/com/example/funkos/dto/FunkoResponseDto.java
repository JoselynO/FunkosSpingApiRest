package com.example.funkos.dto;

import com.example.funkos.models.Funko;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Data
@Schema(description = "Funko a devolver")
public class FunkoResponseDto {
    @Schema(description = "Identificador del funko", example = "2")
    private Long id;
    @Schema(description = "Nombre del funko", example = "Harry Potter")
    private String nombre;
    @Schema(description = "Precio del funko", example = "89.99")
    private double precio;
    @Schema(description = "Cantidad del funko", example = "5")
    private int cantidad;
    @Schema(description = "Imagen del funko", example = Funko.IMAGE_DEFAULT)
    private String imagen;
    @Schema(description = "Categoria del funko", example = "DISNEY")
    private String categoria;
    @Schema(description = "Fecha de creacion del funko", example = "2022-12-12T00:00:00.000Z")
    private LocalDateTime fechaDeCreacion;
    @Schema(description = "Fecha de actualizacion del funko", example = "2022-12-12T00:00:00.000Z")
    private LocalDateTime fechaDeActualizacion;
}
