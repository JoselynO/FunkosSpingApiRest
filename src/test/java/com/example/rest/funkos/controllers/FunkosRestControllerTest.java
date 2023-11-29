package com.example.rest.funkos.controllers;




import com.example.categoria.models.Categoria;
import com.example.funkos.dto.FunkoResponseDto;
import com.example.funkos.exceptions.FunkoNotFound;
import com.example.funkos.models.Funko;
import com.example.funkos.services.FunkoService;
import com.example.utils.pagination.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class) // Extensión de Mockito para usarlo
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class FunkosRestControllerTest {
    private final String myEndpoint = "/v1/funkos";

    private final Categoria categoria = new Categoria(1L, "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko1 = new Funko(1l, "TEST-1", 10.99,5, "test1.png", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    private final Funko funko2 = new Funko(2l, "TEST-2", 11.99,6, "test2.png", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    private final FunkoResponseDto funkoResponse1 = FunkoResponseDto.builder()
            .id(1l)
            .nombre("TEST-3")
            .precio(12.99)
            .cantidad(9)
            .imagen("test3.png")
            .categoria(categoria.getNombre())
            .fechaDeCreacion(LocalDateTime.now())
            .fechaDeActualizacion(LocalDateTime.now())
            .build();

    private final FunkoResponseDto funkoResponse2 = FunkoResponseDto.builder()
            .id(2l)
            .nombre("TEST-4")
            .precio(13.99)
            .cantidad(10)
            .imagen("test4.png")
            .categoria(categoria.getNombre())
            .fechaDeCreacion(LocalDateTime.now())
            .fechaDeActualizacion(LocalDateTime.now())
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FunkoService funkosService;

    @Autowired
    public FunkosRestControllerTest(FunkoService funkosService){
        this.funkosService = funkosService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllFunkos() throws Exception {
        List<Funko> funkoList = List.of(funko1, funko2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        when(funkosService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(funkosService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunkoByCategoria() throws Exception {
        List<Funko> funkoList = List.of(funko1, funko2);
        var localEndpoint = myEndpoint + "?categoria=DISNEY";

        Optional<String> categoria = Optional.of("DISNEY");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        // Arrange
        when(funkosService.findAll(Optional.empty(), categoria, Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(funkosService, times(1)).findAll(Optional.empty(), categoria, Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunkosNombre() throws Exception {
        List<Funko> funkoList = List.of(funko2);
        String localEndPoint = myEndpoint + "?nombre=TEST-2";

        Optional<String> nombreFunkos = Optional.of("TEST-2");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        // Arrange
        when(funkosService.findAll(nombreFunkos, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> resultado = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, resultado.content().size())
        );

        // Verify
        verify(funkosService, times(1)).findAll(nombreFunkos, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunkosPrecio() throws Exception {
        List<Funko> funkoList = List.of(funko1, funko2);
        String localEndPoint = myEndpoint + "?precioMax=11.99";

        Optional<Double> precioMax = Optional.of(11.99);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        // Arrange
        when(funkosService.findAll(Optional.empty(), Optional.empty(), precioMax, Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> resultado = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2,resultado.content().size())
        );

        // Verify
        verify(funkosService, times(1)).findAll(Optional.empty(), Optional.empty(), precioMax, Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunkosCantidad() throws Exception {
        List<Funko> funkoList = List.of(funko1, funko2);
        String localEndPoint = myEndpoint + "?cantidadMin=5";

        Optional<Integer> cantidadMin = Optional.of(5);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        // Arrange
        when(funkosService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), cantidadMin, Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(funkosService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), cantidadMin, Optional.empty(), pageable);
    }

    @Test
    void getFunkoById() throws Exception {
        // Arrange
        String localEndPoint = myEndpoint + "/2";

        when(funkosService.findById(2L)).thenReturn(funko2);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko resultado = mapper.readValue(response.getContentAsString(), Funko.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko2, resultado)
        );
    }

    @Test
    void getAllFunkosActivo() throws Exception {
        List<Funko> funkoList = List.of(funko1, funko2);
        String localEndPoint = myEndpoint + "?activo=true";

        Optional<Boolean> activo = Optional.of(true);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        // Arrange
        when(funkosService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), activo, pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> resultado = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });


        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, resultado.content().size())
        );

        // Verify
        verify(funkosService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), activo, pageable);
    }

    @Test
    void getFunkoByIdFalse() throws Exception {
        // Arrange
        String localEndPoint = myEndpoint + "/1";

        when(funkosService.findById(2L)).thenThrow(new FunkoNotFound(2L));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());

        // verify
        verify(funkosService, times(1)).findById(2L);
    }


    /* @Test
   void createFunko() throws Exception{
        // Arrange
        FunkoCreateDto funkoCreateDto =  FunkoCreateDto.builder()
                .nombre("Test6")
                .precio(8.99)
                .cantidad(15)
                .imagen("test6.jpg")
                .categoria("DISNEY")
                .build();

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunkoCreateDto.write(funkoCreateDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        // Assert
        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(funko1, res)
        );

        // Verify
        verify(funkosService, times(1)).save(funkoCreateDto);
    }*/

}


