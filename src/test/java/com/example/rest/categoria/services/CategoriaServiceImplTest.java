package com.example.rest.categoria.services;

import com.example.categoria.dto.CategoriaDto;
import com.example.categoria.exceptions.CategoriaNotFound;
import com.example.categoria.mappers.CategoriaMapper;
import com.example.categoria.models.Categoria;
import com.example.categoria.repositories.CategoriaRepository;
import com.example.categoria.services.CategoriaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceImplTest {

    private Categoria categoria1 = new Categoria(1L, "SERIES", LocalDateTime.now(), LocalDateTime.now(), true);
    private Categoria categoria2 = new Categoria(2L, "PELICULAS", LocalDateTime.now(), LocalDateTime.now(), true);

    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private CategoriaMapper categoriaMapper;
    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void findAll(){
        // Arrange
        List<Categoria> categoriaList = List.of(categoria1, categoria2);
        String categoriaNombre = null;

        when(categoriaRepository.findAll()).thenReturn(categoriaList);

        // Act
        List <Categoria> resultado = categoriaService.findAll(categoriaNombre);

        // Assert
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertFalse(resultado.isEmpty()),
                () -> assertEquals(2, resultado.size())
        );

        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void findAllByName(){
        // Arrange
        List<Categoria> categoriaList = List.of(categoria1);
        String categoriaNombre = "SERIES";

        when(categoriaRepository.findAllByNombreContainingIgnoreCase(categoriaNombre)).thenReturn(categoriaList);

        // Act
        List <Categoria> res = categoriaService.findAll(categoriaNombre);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty()),
                () -> assertEquals(1, res.size()),
                () -> assertEquals(categoriaNombre, res.get(0).getNombre())
        );
        verify(categoriaRepository, times(1)).findAllByNombreContainingIgnoreCase(categoriaNombre);
    }
    @Test
    void findById(){
        // Arrange
        Long id = 2L;

        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria2));

        // Act
        Categoria res = categoriaService.findById(2L);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(2L, res.getId()),
                () -> assertEquals(categoria2, res)
        );
        verify(categoriaRepository, times(1)).findById(2L);
    }

    @Test
    void findByIdFalse(){
        // Arrange
        Long id = 20L;

        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> categoriaService.findById(id));

        assertEquals("Categoria con id " + id + " no encontrada", res.getMessage());

        verify(categoriaRepository, times(1)).findById(id);
    }


    @Test
    void findByName(){
        // Arrange
        String nombreCategoria = "PELICULAS";

        when(categoriaRepository.findByNombreEqualsIgnoreCase(nombreCategoria)).thenReturn(Optional.of(categoria2));

        // Act
        Categoria res = categoriaService.findByNombre(nombreCategoria);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(nombreCategoria, res.getNombre()),
                () -> assertEquals(categoria2, res)
        );
        verify(categoriaRepository, times(1)).findByNombreEqualsIgnoreCase(nombreCategoria);
    }

    @Test
    void findByNombreCategoriaFalse(){
        // Arrange
        String nombreCategoria = "ALINA";

        when(categoriaRepository.findByNombreEqualsIgnoreCase(nombreCategoria)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> categoriaService.findByNombre(nombreCategoria));

        assertEquals("Categoria con nombre " + nombreCategoria + " no encontrada", res.getMessage());

        verify(categoriaRepository, times(1)).findByNombreEqualsIgnoreCase(nombreCategoria);
    }

    @Test
    void save(){
        // Arrange
        Categoria nuevaCategoria = new Categoria(3L, "MARVEL", LocalDateTime.now(), LocalDateTime.now(), true);
        CategoriaDto categoriaDto = new CategoriaDto("MARVEL", true);

        when(categoriaRepository.save(nuevaCategoria)).thenReturn(nuevaCategoria);
        when(categoriaMapper.toCategoria(categoriaDto)).thenReturn(nuevaCategoria);

        // Act
        Categoria res = categoriaService.save(categoriaDto);

        // Assert
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(nuevaCategoria, res)
        );

        verify(categoriaRepository, times(1)).save(nuevaCategoria);
        verify(categoriaMapper, times(1)).toCategoria(categoriaDto);
    }

    @Test
    void update(){
        // Arrange
        Categoria categoriaActual = categoria2;
        CategoriaDto categoriaDto = new CategoriaDto("MARVEL", true);

        when(categoriaRepository.findById(any(Long.class))).thenReturn(Optional.of(categoriaActual));
        when(categoriaMapper.toCategoria(categoriaDto, categoriaActual)).thenReturn(categoriaActual);
        when(categoriaRepository.save(categoriaActual)).thenReturn(categoriaActual);

        // Act
        Categoria categoriaActualizada = categoriaService.update(categoriaActual.getId(), categoriaDto);

        // Assert
        assertAll(
                () -> assertNotNull(categoriaActualizada),
                () -> assertEquals(categoriaActual, categoriaActualizada)
        );
    }

    @Test
    void updateIdFalse(){
        // Arrange
        CategoriaDto categoriaDto = new CategoriaDto("SUPERHEROES", true);

        when(categoriaRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> categoriaService.update(2L, categoriaDto));

        assertEquals("Categoria con id " + 2L + " no encontrada", res.getMessage());

        verify(categoriaRepository, times(1)).findById(2L);
    }

    @Test
    void deleteById(){
        // Arrange
        Long id = 2L;

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria2));

        // Act
        categoriaService.deleteById(id);

        verify(categoriaRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteByIdFalse(){
        // Arrange
        Long id = 20L;

        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(CategoriaNotFound.class, () ->  categoriaService.deleteById(id));

        assertEquals("Categoria con id " + id + " no encontrada", res.getMessage());

        verify(categoriaRepository, times(1)).findById(id);
        verify(categoriaRepository, times(0)).deleteById(id);
    }

}