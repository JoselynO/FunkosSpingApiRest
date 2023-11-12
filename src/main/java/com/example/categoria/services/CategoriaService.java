package com.example.categoria.services;


import com.example.categoria.dto.CategoriaDto;
import com.example.categoria.models.Categoria;

import java.util.List;

public interface CategoriaService {
  List<Categoria> findAll(String nombre);

  Categoria findByNombre(String nombre);
  Categoria findById(Long id);
  Categoria save(CategoriaDto categoriaDto);
  Categoria update(Long id, CategoriaDto categoriaDto);
  void deleteById(Long id);

}
