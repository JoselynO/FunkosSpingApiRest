package com.example.funkos.models;

import com.example.categoria.models.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name = "FUNKOS")

public class Funko {
    @Builder.Default
    public static final String IMAGE_DEFAULT = "https://via.placeholder.com/150";
    @Id // Indicamos que es el ID de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank( message = "El nombre no puede estar vacio")
    private String nombre;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;

    @Min(value = 0, message = "La cantidad no puede ser menor a 0")
    private Integer cantidad;

    @Column(columnDefinition = "TEXT default '" + IMAGE_DEFAULT + "'")
    private String imagen;

    @Temporal(TemporalType.TIMESTAMP) // Indicamos que es un campo de tipo fecha y hora
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime fechaDeCreacion;

    @Temporal(TemporalType.TIMESTAMP) // Indicamos que es un campo de tipo fecha y hora
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime fechaDeActualizacion;

    @Column(columnDefinition = "boolean default true")
    private final Boolean activo;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;



    }

