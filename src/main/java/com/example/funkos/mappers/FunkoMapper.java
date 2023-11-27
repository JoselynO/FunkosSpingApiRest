package com.example.funkos.mappers;

import com.example.categoria.models.Categoria;
import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoResponseDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.models.Funko;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class FunkoMapper {

    public Funko toFunko(FunkoCreateDto dto, Categoria categoria){
        return new Funko(
                null,
                dto.getNombre(),
                dto.getPrecio(),
                dto.getCantidad(),
                dto.getImagen() != null ? dto.getImagen() : Funko.IMAGE_DEFAULT,
                LocalDateTime.now(),
                LocalDateTime.now(),
                true,
                categoria
        );
    }

    public Funko toFunko(FunkoUpdateDto dto, Funko funko, Categoria categoria){
        return new Funko(
                funko.getId(),
                dto.getNombre() != null ? dto.getNombre() : funko.getNombre(),
                dto.getPrecio() != null ? dto.getPrecio() : funko.getPrecio(),
                dto.getCantidad() != null ? dto.getCantidad() : funko.getCantidad(),
                dto.getImagen() != null ? dto.getImagen() : funko.getImagen(),
                funko.getFechaDeCreacion(),
                LocalDateTime.now(),
                dto.getActivo() != null ? dto.getActivo(): funko.getActivo(),
                categoria
        );
    }

    public FunkoResponseDto toFunkoResponseDto(Funko funko){
        return FunkoResponseDto.builder()
                .id(funko.getId())
                .nombre(funko.getNombre())
                .precio(funko.getPrecio())
                .cantidad(funko.getCantidad())
                .imagen(funko.getImagen())
                .categoria(funko.getCategoria().getNombre())
                .fechaDeCreacion(funko.getFechaDeCreacion())
                .fechaDeActualizacion(funko.getFechaDeActualizacion())
                .build();
    }

}
