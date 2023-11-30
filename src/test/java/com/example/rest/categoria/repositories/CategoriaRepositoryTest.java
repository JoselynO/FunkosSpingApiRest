package com.example.rest.categoria.repositories;

import com.example.categoria.models.Categoria;
import com.example.categoria.repositories.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "spring.sql.init.mode=never")
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoriaRepositoryTest {

    Categoria categoria = new Categoria(null, "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);

    @Autowired
    private CategoriaRepository categoriasRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        entityManager.merge(categoria);
        entityManager.flush();
    }


    @Test
    void findAll() {
        // Act
        List<Categoria> listaCategories = categoriasRepository.findAll();

        // Assert
        assertAll(
                () -> assertNotNull(listaCategories),
                () -> assertFalse(listaCategories.isEmpty())
        );
    }

    @Test
    void findAllByNombre(){
        // Act
        String nombreEsperado = "DISNEY";
        List<Categoria> listaCategoria = categoriasRepository.findAllByNombreContainingIgnoreCase("disney");

        // Assert
        assertAll(
                () -> assertNotNull( listaCategoria),
                () -> assertFalse( listaCategoria.isEmpty()),
                () -> assertEquals(nombreEsperado,  listaCategoria.get(0).getNombre())
        );
    }

    @Test
    void findByID() {
        // Act
        Categoria categoria = categoriasRepository.findById(1L).orElse(null);

        // Assert
        assertAll(
                () -> assertNotNull(categoria),
                () -> assertEquals("DISNEY", categoria.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        // Act
        Categoria categoria = categoriasRepository.findById(99L).orElse(null);

        // Assert
        assertNull(categoria);
    }


    @Test
    void save() {
        // Act
        Categoria nuevaCategoria = new Categoria(null, "SERIES", LocalDateTime.now(), LocalDateTime.now(), true);
        Categoria guardarCategoria = categoriasRepository.save(nuevaCategoria);

        // Assert
        assertAll("save",
                () -> assertNotNull(guardarCategoria),
                () -> assertEquals("SERIES", guardarCategoria.getNombre())
        );
    }


    @Test
    void update() {
        // Act
        Categoria categoria = categoriasRepository.findById(1L).orElse(null);
        Categoria categoriaUpdate = categoriasRepository.save(new Categoria(categoria.getId(), "PELICULAS", LocalDateTime.now(), LocalDateTime.now(), true));

        // Assert
        assertAll("update",
                () -> assertNotNull(categoriaUpdate),
                () -> assertEquals("PELICULAS", categoriaUpdate.getNombre())
        );
    }

    @Test
    void delete() {
        // Act
        Categoria categoria = categoriasRepository.findById(1L).orElse(null);
        categoriasRepository.deleteById(categoria.getId());
        Categoria categoriaDelete = categoriasRepository.findById(1L).orElse(null);

        // Assert
        assertNull(categoriaDelete);
    }

}