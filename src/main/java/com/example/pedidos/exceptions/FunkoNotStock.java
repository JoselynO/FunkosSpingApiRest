package com.example.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FunkoNotStock extends  PedidoException{
        public FunkoNotStock(Long id) {
            super("Funko con id " + id + " no tiene stock suficiente");
        }
}
