package com.example.rest.categoria.controller;

import com.example.categoria.dto.CategoriaDto;
import com.example.categoria.models.Categoria;
import com.example.categoria.services.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class CategoriaRestControllerTest {

    private final String myEndpoint = "/v1/categorias";
    private final Categoria categoria = new Categoria(1L, "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mvc;
    @MockBean
    private CategoriaService categoriasService;
    @Autowired
    private JacksonTester<CategoriaDto> jsonCategoriaDto;

    @Autowired
    public CategoriaRestControllerTest(CategoriaService categoriasService){
        this.categoriasService = categoriasService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllCategorias() throws Exception {
        // Arrange
        List<Categoria> categoriaList = List.of(categoria);

        when(categoriasService.findAll(null)).thenReturn(categoriaList);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Categoria> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Categoria.class));

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty()),
                () -> assertEquals(1, res.size()),
                () -> assertEquals(categoria, res.get(0))
        );

        verify(categoriasService, times(1)).findAll(null);
    }

    @Test
    void getAllCategoriasByNombre() throws Exception {
        // Arrange
        List<Categoria> categoriaList = List.of(categoria);
        String nombreCategoria = "DISNEY";
        String myLocalEndPoint = myEndpoint + "?nombre=DISNEY";

        when(categoriasService.findAll(nombreCategoria)).thenReturn(categoriaList);

        MockHttpServletResponse response = mvc.perform(
                        get(myLocalEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Categoria> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Categoria.class));

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty()),
                () -> assertEquals(1, res.size()),
                () -> assertEquals(categoria, res.get(0))
        );

        verify(categoriasService, times(1)).findAll(nombreCategoria);
    }
}
