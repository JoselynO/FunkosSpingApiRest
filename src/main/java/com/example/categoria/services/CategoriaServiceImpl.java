package com.example.categoria.services;

import com.example.categoria.dto.CategoriaDto;
import com.example.categoria.exceptions.CategoriaBadRequest;
import com.example.categoria.exceptions.CategoriaConflict;
import com.example.categoria.exceptions.CategoriaNotFound;
import com.example.categoria.mappers.CategoriaMapper;
import com.example.categoria.models.Categoria;
import com.example.categoria.repositories.CategoriaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }


    @Override
    public List<Categoria> findAll(String nombre) {
        log.info("Buscando Categoria por nombre " + nombre);
        if (nombre == null || nombre.isEmpty()) {
            return categoriaRepository.findAll();
        } else {
            return categoriaRepository.findAllByNombreContainingIgnoreCase(nombre);
        }
    }

    @Override
    public Categoria findByNombre(String nombre) {
        log.info("Buscando Categoria por nombre: " + nombre);
        return categoriaRepository.findByNombreEqualsIgnoreCase(nombre).orElseThrow(() -> new CategoriaNotFound(nombre));
    }

    @Override
    @Cacheable
    public Categoria findById(Long id) {
        log.info("Buscando Categoria por id: " + id);
        return categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
    }

    @Override
    @CachePut
    public Categoria save(CategoriaDto categoriaDto) {
        log.info("Guardando categoria: " + categoriaDto);
        if(categoriaRepository.findByNombreEqualsIgnoreCase(categoriaDto.getNombre()).isPresent()){
            throw new CategoriaBadRequest(categoriaDto.getNombre());
        }
        return categoriaRepository.save(categoriaMapper.toCategoria(categoriaDto));
    }

    @Override
    @CachePut
    public Categoria update(Long id, CategoriaDto categoriaDto) {
        log.info("Actualizando categoria: " + categoriaDto);
        Categoria categoriaActual = findById(id);
        return categoriaRepository.save(categoriaMapper.toCategoria(categoriaDto, categoriaActual));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando categoria por id: " + id);
        Categoria categoria = findById(id);
        if (categoriaRepository.existsFunkoById(id)) {
            log.warn("No se puede borrar la categoría con id: " + id + " porque tiene categoria asociados");
            throw new CategoriaConflict("No se puede borrar la categoría con id " + id + " porque tiene categoris asociados");
        } else {
            categoriaRepository.deleteById(id);
        }
    }
}

