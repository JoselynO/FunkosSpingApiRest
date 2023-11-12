package com.example.categoria.repositories;

import com.example.categoria.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombreEqualsIgnoreCase(String nombre);
    Optional<Categoria> findByNombreEqualsIgnoreCaseAndActivoTrue(String nombre);
    List<Categoria> findAllByNombreContainingIgnoreCase(String nombre);
    List<Categoria> findAllByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    List<Categoria> findByActivo(Boolean isActivo);
    @Modifying
    @Query("UPDATE Categoria c SET c.activo = false WHERE c.id = :id")

    void updateActivoToFalseById(Long id);

    @Query("SELECT CASE WHEN COUNT(funko) > 0 THEN true ELSE false END FROM Funko funko WHERE funko.categoria.id = :id")
    Boolean existsFunkoById(Long id);

}
