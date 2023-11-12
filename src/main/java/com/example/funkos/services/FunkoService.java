package com.example.funkos.services;

import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.models.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FunkoService {
    Page<Funko> findAll(Optional<String> nombre, Optional <String> categoria, Optional<Double> precioMax, Optional<Integer> cantidadMin, Optional<Boolean> activo, Pageable pageable);

    Funko findById(Long id);

    Funko save(FunkoCreateDto funkoCreateDto);

    Funko update(Long id, FunkoUpdateDto funkoUpdateDto);

    void deleteById(Long id);
    Funko updateImage(Long id, MultipartFile image);
}
