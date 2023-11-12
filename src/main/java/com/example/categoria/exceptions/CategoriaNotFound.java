package com.example.categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoriaNotFound extends CategoriaException{
    public CategoriaNotFound(Long id) {
        super("Categoria con id " + id + " no encontrada");
    }

    public CategoriaNotFound(String categoria) {
        super("Categoria con nombre " + categoria + " no encontrada");
    }
}
