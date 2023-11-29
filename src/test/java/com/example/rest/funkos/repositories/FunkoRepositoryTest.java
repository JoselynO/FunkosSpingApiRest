package com.example.rest.funkos.repositories;

import com.example.categoria.models.Categoria;
import com.example.funkos.models.Funko;
import com.example.funkos.repositories.FunkosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class FunkoRepositoryTest {

    private final Categoria categoria = new Categoria(null, "TEST", LocalDateTime.now(), LocalDateTime.now(),false);

    private final Funko funko1 = new Funko(
            null, "batman",44.99,
            3, "batman.jpj",
            LocalDateTime.now(), LocalDateTime.now(),
            false,categoria);

    private final Funko funko2 = new Funko(
            null, "poderosas",50.99,
            9, "poderosas.jpj",
            LocalDateTime.now(), LocalDateTime.now(),
            false,categoria);

    @Autowired
    private FunkosRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp(){
        entityManager.merge(categoria);
        entityManager.flush();
        entityManager.merge(funko1);
        entityManager.merge(funko2);
        entityManager.flush();
    }

    @Test
    void findAll(){
        List<Funko> funkos = repository.findAll();

        assertAll("findAll",
                () -> assertNotNull(funkos),
                () -> assertFalse(funkos.isEmpty()),
                () -> assertTrue(funkos.size() >= 2)
        );
    }


    @Test
    void findById_true(){
        Long id = 1L;
        Optional<Funko> funkoEncontrado = repository.findById(id);

        assertAll(
                () -> assertNotNull(funkoEncontrado),
                () -> assertTrue(funkoEncontrado.isPresent()),
                () -> assertEquals(1L, funkoEncontrado.get().getId())
        );
    }

    @Test
    void findById_false(){
        Long id = 99L;
        Optional<Funko> funkoEncontrado = repository.findById(id);

        assertAll(
                () -> assertNotNull(funkoEncontrado),
                () -> assertTrue(funkoEncontrado.isEmpty())
        );
    }

    @Test
    void save(){
        Funko funko3 = new Funko(
                null, "rapunzel",18.99,
                3, "rapunzel.jpg",
                LocalDateTime.now(), LocalDateTime.now(),
                false,categoria);

        Funko guardarFunko = repository.save(funko3);
        List<Funko> funkos = repository.findAll();

        assertAll(
                () -> assertNotNull(guardarFunko),
                () -> assertTrue(repository.existsById(guardarFunko.getId())),
                () -> assertTrue(funkos.size()>=2)
        );
    }

    @Test
    void save_exist(){
        Funko funko3 = new Funko(
                1L, "rapunzel",18.99,
                3, "rapunzel.jpg",
                LocalDateTime.now(), LocalDateTime.now(),
                false,categoria);

        Funko guardarFunko = repository.save(funko3);
        List<Funko> funkos = repository.findAll();

        assertAll(
                () -> assertEquals(funko3, guardarFunko),
                () -> assertNotNull(guardarFunko),
                () -> assertTrue(repository.existsById(guardarFunko.getId())),
                () -> assertTrue(funkos.size()>=2)
        );
    }

    @Test
    void deleteById(){
        Long id = 1L;
        repository.deleteById(id);
        List<Funko> funkos = repository.findAll();

        assertAll("deleteById",
                () -> assertFalse(repository.existsById(id)),
                ()-> assertFalse(funkos.isEmpty())
        );
    }

}
