package com.example.funkos.repositories;

import com.example.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FunkosRepository  extends JpaRepository<Funko, Long> {

    @Query("SELECT funko FROM Funko funko WHERE LOWER(funko.categoria.nombre) LIKE %:categoria%")
    List<Funko> findByCategoriaContainsIgnoreCase(String categoria);

    @Query("SELECT funko FROM Funko funko WHERE LOWER(funko.categoria.nombre) LIKE %:categoria% AND funko.activo = true")
    List<Funko> findByCategoriaContainsIgnoreCaseAndActivoTrue(String categoria);

    List<Funko> findByActivo(Boolean isActivo);
    @Modifying // Para indicar que es una consulta de actualización
    @Query("UPDATE Funko funkos SET funkos.activo = false WHERE funkos.id = :id")
    void updateActivoToFalseById(Long id);
}
