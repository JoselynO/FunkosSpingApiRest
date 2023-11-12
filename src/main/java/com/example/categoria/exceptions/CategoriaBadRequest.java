package com.example.categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoriaBadRequest extends CategoriaException{
    public CategoriaBadRequest(String categoria) {
        super("La categoria: " + categoria + " ya existe en la BD");
    }
}
