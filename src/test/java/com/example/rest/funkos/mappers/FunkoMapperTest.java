package com.example.rest.funkos.mappers;

import com.example.categoria.models.Categoria;
import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoResponseDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.mappers.FunkoMapper;
import com.example.funkos.models.Funko;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FunkoMapperTest {
    private final FunkoMapper funkosMapper = new FunkoMapper();
    Categoria categoria1 = new Categoria(1L, "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);
    Categoria categoria2 = new Categoria(2L, "SERIE", LocalDateTime.now(), LocalDateTime.now(), true);

    @Test
    void testFunkoCreate(){
        // Arrange
        FunkoCreateDto funkoCreateDto = FunkoCreateDto.builder()
                .nombre("Funko1")
                .precio(9.99)
                .cantidad(9)
                .imagen("funkotest.jpg")
                .categoria("DISNEY")
                .build();
        // Act
        Funko funkoNuevo = funkosMapper.toFunko(funkoCreateDto, categoria1);

        // Assert
        assertAll(
                () -> assertNull(funkoNuevo.getId()),
                () -> assertEquals(funkoCreateDto.getNombre(), funkoNuevo.getNombre()),
                () -> assertEquals(funkoCreateDto.getPrecio(), funkoNuevo.getPrecio()),
                () -> assertEquals(funkoCreateDto.getCantidad(), funkoNuevo.getCantidad()),
                () -> assertEquals(funkoCreateDto.getImagen(), funkoNuevo.getImagen()),
                () -> assertEquals(funkoCreateDto.getCategoria(), funkoNuevo.getCategoria().getNombre()),
                () -> assertNotNull(funkoNuevo.getFechaDeActualizacion()),
                () -> assertNotNull(funkoNuevo.getFechaDeCreacion()),
                () -> assertTrue(funkoNuevo.getActivo())
        );
    }

    @Test
    void testFunkoUpdate(){
        // Arrange
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("Funko")
                .precio(8.99)
                .cantidad(5)
                .imagen("hola.png")
                .categoria("DISNEY")
                .build();
        Funko funko = new Funko(1l, "TEST", 1.99,2, "test.png", LocalDateTime.now(), LocalDateTime.now(), true, categoria1);

        // Act
        Funko funkoActualizado = funkosMapper.toFunko(funkoUpdateDto, funko , categoria1);

        // Assert
        assertAll(
                () -> assertEquals(funkoUpdateDto.getNombre(), funkoActualizado.getNombre()),
                () -> assertEquals(funkoUpdateDto.getPrecio(), funkoActualizado.getPrecio()),
                () -> assertEquals(funkoUpdateDto.getCantidad(), funkoActualizado.getCantidad()),
                () -> assertEquals(funkoUpdateDto.getImagen(), funkoActualizado.getImagen()),
                () -> assertEquals(funkoUpdateDto.getCategoria(), funkoActualizado.getCategoria().getNombre()),
                () -> assertEquals(funko.getId(), funkoActualizado.getId()),
                () -> assertEquals(funko.getFechaDeCreacion(), funkoActualizado.getFechaDeCreacion()),
                () -> assertNotNull(funkoActualizado.getFechaDeActualizacion())
        );
    }

    @Test
    void toResponseDtoTest() {
        // Arrange
        Funko funko = new Funko(1L, "FunkoTest3", 23.88, 20, "funkotest3.jpg", LocalDateTime.now(), LocalDateTime.now(), true, categoria1);

        // Act
        FunkoResponseDto funkoResponseDto = funkosMapper.toFunkoResponseDto(funko);

        // Assert
        assertAll(
                () -> assertEquals(funko.getNombre(), funkoResponseDto.getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoResponseDto.getPrecio()),
                () -> assertEquals(funko.getCantidad(), funkoResponseDto.getCantidad()),
                () -> assertEquals(funko.getImagen(), funkoResponseDto.getImagen()),
                () -> assertEquals(funko.getCategoria().getNombre(), funkoResponseDto.getCategoria()),
                () -> assertEquals(funko.getId(), funkoResponseDto.getId()),
                () -> assertEquals(funko.getFechaDeCreacion(), funkoResponseDto.getFechaDeCreacion()),
                () -> assertEquals(funko.getFechaDeActualizacion(), funkoResponseDto.getFechaDeActualizacion())
        );
    }
}
