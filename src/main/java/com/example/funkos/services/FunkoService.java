package com.example.funkos.services;

import com.example.categoria.models.Categoria;
import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.models.Funko;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FunkoService {
    List<Funko> findAll( String categoria);

    Funko findById(Long id);

    Funko save(FunkoCreateDto funkoCreateDto);

    Funko update(Long id, FunkoUpdateDto funkoUpdateDto);

    void deleteById(Long id);
    Funko updateImage(Long id, MultipartFile image);
}
