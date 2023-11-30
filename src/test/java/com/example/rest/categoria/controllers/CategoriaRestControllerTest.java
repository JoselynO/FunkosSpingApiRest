package com.example.rest.categoria.controllers;

import com.example.categoria.dto.CategoriaDto;
import com.example.categoria.exceptions.CategoriaConflict;
import com.example.categoria.exceptions.CategoriaNotFound;
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

    @Test
    void getCategoriaById() throws Exception {
        // Arrange
        Long id = 1L;
        String myLocalEndPoint = myEndpoint + "/1";

        when(categoriasService.findById(id)).thenReturn(categoria);

        MockHttpServletResponse response = mvc.perform(
                        get(myLocalEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria categoriaEncontrada = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertNotNull(categoriaEncontrada),
                () -> assertEquals(categoria, categoriaEncontrada)
        );

        verify(categoriasService, times(1)).findById(id);
    }

    @Test
    void getCategoriaByIdFalse() throws Exception {
        // Arrange
        Long id = 20L;
        String myLocalEndPoint = myEndpoint + "/20";

        when(categoriasService.findById(id)).thenThrow(new CategoriaNotFound(id));

        MockHttpServletResponse response = mvc.perform(
                        get(myLocalEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(categoriasService, times(1)).findById(id);
    }

    @Test
    void createCategoria() throws Exception {
        // Arrange
        CategoriaDto categoriaDto = new CategoriaDto("MARVEL", true);
        Categoria categoria2 = new Categoria(2L, "MARVEL", LocalDateTime.now(), LocalDateTime.now(), true);

        when(categoriasService.save(categoriaDto)).thenReturn(categoria2);

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria savedCategoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(categoria2, savedCategoria)
        );

        verify(categoriasService, times(1)).save(categoriaDto);
    }

    @Test
    void createCategoriaConflict() throws Exception {
        // Arrange
        CategoriaDto categoriaDto = new CategoriaDto("OTROS", true);

        when(categoriasService.save(categoriaDto)).thenThrow(new CategoriaConflict(categoriaDto.getNombre()));

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(409, response.getStatus())
        );
    }

  /*  @Test
    void createCategoriaBadRequest() throws Exception {
        // Arrange
        CategoriaDto categoriaDto = new CategoriaDto("PEPE_EL_GRILLO", true);

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("La categoria solo puede ser: SERIE, DISNEY, SUPERHEROES, PELICULAS U OTROS"))
        );
    }*/

    @Test
    void updateCategoria() throws Exception {
        // Arrange
        CategoriaDto categoriaDto = new CategoriaDto("MARVEL", true);
        String myLocalEndpoint = myEndpoint + "/1";

        when(categoriasService.findById(1L)).thenReturn(categoria);
        when(categoriasService.update(1L, categoriaDto)).thenReturn(categoria);

        MockHttpServletResponse response = mvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria categoriaActualizada = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoria, categoriaActualizada)
        );

        verify(categoriasService, times(1)).update(1L, categoriaDto);
    }

    @Test
    void updateCategoriaIdFalse() throws Exception {
        // Arrange
        CategoriaDto categoriaDto = new CategoriaDto("DISNEY", true);
        String myLocalEndpoint = myEndpoint + "/20";
        Long id = 20L;

        when(categoriasService.update(id, categoriaDto)).thenThrow(new CategoriaNotFound(id));

        MockHttpServletResponse response = mvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(categoriasService, times(1)).update(id, categoriaDto);
    }

  /*  @Test
    void updateCategoriaBadRequest() throws Exception {
        // Arrange
        CategoriaDto categoriaDto = new CategoriaDto("ALINA", true);
        String myLocalEndpoint = myEndpoint + "/1";
        Long id = 1L;

        MockHttpServletResponse response = mvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Categoria con nombre " + categoriaDto + " no encontrada"))
        );

        verify(categoriasService, times(0)).update(id, categoriaDto);
    }*/


}
