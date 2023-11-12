package com.example.funkos.repositories;

import com.example.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FunkosRepository  extends JpaRepository<Funko, Long>, JpaSpecificationExecutor<Funko> {

    List<Funko> findByActivo(Boolean activo);

    @Modifying
    @Query("UPDATE Funko f SET f.activo = false WHERE f.id = :id")
    void updateActivoToFalseById(Long id);
}
