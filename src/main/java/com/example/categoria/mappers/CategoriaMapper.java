package com.example.categoria.mappers;

import com.example.categoria.dto.CategoriaDto;
import com.example.categoria.models.Categoria;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriaMapper {
    public Categoria toCategoria(CategoriaDto dto){
        return new Categoria(
                null,
                dto.getNombre().toUpperCase(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );
    }

    public Categoria toCategoria(CategoriaDto dto, Categoria categoria) {
        return new Categoria(
                categoria.getId(),
                dto.getNombre() != null ? dto.getNombre() : categoria.getNombre(),
                categoria.getCreatedAt(),
                LocalDateTime.now(),
                dto.getActivo() != null ? dto.getActivo() : categoria.getActivo()
        );
    }}
