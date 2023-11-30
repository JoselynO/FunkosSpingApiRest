package com.example.rest.categoria.mappers;

import com.example.categoria.dto.CategoriaDto;
import com.example.categoria.mappers.CategoriaMapper;
import com.example.categoria.models.Categoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaMapperTest {
    private final CategoriaMapper mapper = new CategoriaMapper();

    private final CategoriaDto categoriaDto = new CategoriaDto("MARVEL", false);
    private final Categoria categoria = new Categoria(1L, "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);

    @Test
    void testCategoriaCreate() {
        // Act
        Categoria categoriaNew = mapper.toCategoria(categoriaDto);

        // Assert
        assertAll(
                () -> assertEquals(categoriaDto.getNombre(), categoriaNew.getNombre()),
                () -> assertTrue(categoriaNew.getActivo()),
                () -> assertNotNull(categoriaNew.getCreatedAt()),
                () -> assertNotNull(categoriaNew.getUpdatedAt()),
                () -> assertNull(categoriaNew.getId())
        );
    }

    @Test
    void testCategoriaUpdate(){
        // Act
        Categoria categoriaUpdate = mapper.toCategoria(categoriaDto, categoria);

        // Assert
        assertAll(
                () -> assertEquals(categoriaDto.getNombre(), categoriaUpdate.getNombre()),
                () -> assertEquals(categoriaDto.getActivo(), categoriaUpdate.getActivo()),
                () -> assertEquals(categoria.getId(), categoriaUpdate.getId()),
                () -> assertEquals(categoria.getCreatedAt(), categoriaUpdate.getCreatedAt())
        );
    }
}
