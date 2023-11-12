package com.example.notifications.mappers;

import com.example.funkos.models.Funko;
import com.example.notifications.dto.FunkoNotificacionDto;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {
    public FunkoNotificacionDto toFunkoNotificationDto(Funko funko) {
        return new FunkoNotificacionDto(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCantidad(),
                funko.getImagen(),
                funko.getFechaDeCreacion().toString(),
                funko.getFechaDeActualizacion().toString(),
                funko.getActivo(),
                funko.getCategoria().getNombre()
        );
    }
}
