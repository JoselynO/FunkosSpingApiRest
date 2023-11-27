package com.example.websockets.notifications.dto;

public record FunkoNotificacionDto(
        Long id,
        String nombre,
        Double precio,
        Integer cantidad,
        String imagen,
        String fechaCreacion,
        String fechaActualizacion,
        Boolean isActivo,
        String categoria
) {
}
