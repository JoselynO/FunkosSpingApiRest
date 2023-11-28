package com.example.rest.categoria;

import com.example.categoria.models.Categoria;
import com.example.categoria.repositories.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CategoriaRepositoryTest {

    Categoria categoria =  new Categoria(null, "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);

    @Autowired
    private CategoriaRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp(){
        entityManager.merge(categoria);
        entityManager.flush();
    }

    @Test
    void findAll(){
        List<Categoria> categorias = repository.findAll();

        assertAll( "findAll",
                () -> assertNotNull(categorias),
                () -> assertFalse(categorias.isEmpty()),
                () -> assertFalse(categorias.isEmpty())

        );
    }

    @Test
    void findAllByNombre(){
        List<Categoria> categorias = repository.findAllByNombreContainingIgnoreCase("DISNEY");

        assertAll("findAllByNombre",
                () -> assertNotNull(categorias),
                () -> assertFalse(categorias.isEmpty()),
                () -> assertEquals("DISNEY", categorias.get(0).getNombre())
        );
    }

    @Test
    void findByID(){
        Categoria categoriaRes = repository.findById(categoria.getId()).orElse(null);

        assertAll("findByID",
                () -> assertNotNull(categoriaRes),
                () -> assertEquals("DISNEY", categoriaRes.getNombre())
        );
    }

    @Test
    void findByIDFalse(){
        Categoria categoriaRes = repository.findById(99L).orElse(null);

        assertAll("findByIDFalse",
                () -> assertNull(categoriaRes)
        );
    }

    @Test
    void update(){
        Categoria categoriaExiste = repository.findById(categoria.getId()).orElse(null);
        Categoria nuevaCategoria = new Categoria(categoriaExiste.getId(), "SUPERHEROES", LocalDateTime.now(), LocalDateTime.now(), true);
        Categoria updatedCategoria = repository.save(nuevaCategoria);

        assertAll("update",
                () -> assertNotNull(updatedCategoria),
                () -> assertEquals(nuevaCategoria, updatedCategoria)
        );
    }

    @Test
    void delete(){
        Categoria categoriaRes = repository.findById(categoria.getId()).orElse(null);
        repository.delete(categoriaRes);
        Categoria categoriaDelete = repository.findById(categoria.getId()).orElse(null);

        assertAll(
                ()-> assertNull(categoriaDelete)
        );
    }



}
