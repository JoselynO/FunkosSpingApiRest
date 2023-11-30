package com.example.rest.funkos.controllers;
import com.example.categoria.models.Categoria;
import com.example.funkos.controllers.FunkoRestController;
import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.exceptions.FunkoNotFound;
import com.example.funkos.models.Funko;
import com.example.funkos.services.FunkoService;
import com.example.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class FunkoRestControllerMvcTest {
    @Mock
    private FunkoService funkoService;
    @InjectMocks
    private FunkoRestController funkoController;

    private final Categoria categoria = new Categoria(1L, "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko1 = new Funko(1l, "TEST-1", 10.99, 5, "test1.png", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    private final Funko funko2 = new Funko(2l, "TEST-2", 11.99, 6, "test2.png", LocalDateTime.now(), LocalDateTime.now(), true, categoria);

    @Test
    void getAllFunkos() {
        List<Funko> funkoList = List.of(funko1, funko2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        // Arrange
        when(funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);


        ResponseEntity<PageResponse<Funko>> responseEntity = funkoController.getAllProducts(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 0, 10, "id", "asc");

        // Assert
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().content().size());

        // Verify
        verify(funkoService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllFunkoByNombre() {
        Optional<String> nombre = Optional.of("TEST-2");
        List<Funko> expectedFunks = List.of(funko2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(expectedFunks);

        // Arrange
        when(funkoService.findAll(nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);


        ResponseEntity<PageResponse<Funko>> responseEntity = funkoController.getAllProducts(nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 0, 10, "id", "asc");

        // Assert
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().content().size());

        // Verify
        verify(funkoService, times(1)).findAll(nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getFunkoById() {
        // Arrange
        Long id = funko1.getId();

        when(funkoService.findById(id)).thenReturn(funko1);

        ResponseEntity<Funko> responseEntity = funkoController.getFunkoById(id);

        // Assert
        assertAll(
                () -> assertEquals(200, responseEntity.getStatusCode().value()),
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(funko1, responseEntity.getBody())
        );

        // verify
        verify(funkoService, times(1)).findById(id);
    }


    @Test
    void getFunkoByIdFalse() {
        // Arrange
        Long id = 50L;

        when(funkoService.findById(id)).thenThrow(new FunkoNotFound(id));

        // Act
        var result = assertThrows(FunkoNotFound.class, () -> funkoController.getFunkoById(id));
        assertEquals("Funko no encontrado con id: " + id, result.getMessage());

        // Verify
        verify(funkoService, times(1)).findById(id);
    }

    @Test
    void createFunko() {
        // Arrange
        FunkoCreateDto funkoDto = FunkoCreateDto.builder()
                .nombre("Funko3")
                .precio(9.99)
                .cantidad(5)
                .imagen("funko3.jpg")
                .categoria("DISNEY")
                .build();

        when(funkoService.save(funkoDto)).thenReturn(funko2);

        // Act
        ResponseEntity<Funko> result = funkoController.createFunko(funkoDto);

        // Assert
        assertAll(
                () -> assertEquals(201, result.getStatusCode().value()),
                () -> assertEquals(funko2, result.getBody())
        );

        // Verify
        verify(funkoService, times(1)).save(funkoDto);
    }

    @Test
    void updateFunko() {
        // Arrange
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("Funko3")
                .precio(9.99)
                .cantidad(5)
                .imagen("funko3.jpg")
                .categoria("DISNEY")
                .build();

        when(funkoService.update(2L, funkoUpdateDto)).thenReturn(funko2);

        // Act
        ResponseEntity<Funko> result = funkoController.updateFunko(funko2.getId(), funkoUpdateDto);

        // Assert
        assertAll(
                () -> assertEquals(200, result.getStatusCode().value()),
                () -> assertEquals(funko2, result.getBody())
        );

        // Verify
        verify(funkoService, times(1)).update(2L, funkoUpdateDto);
    }


    @Test
    void updateFunko_NotFound() {
        // Arrange
        when(funkoService.update(anyLong(), any())).thenThrow(new FunkoNotFound(10L));

        // Act & Assert
        var result = assertThrows(FunkoNotFound.class, () -> funkoService.update(anyLong(), any()));
        assertEquals("Funko no encontrado con id: " + 10L, result.getMessage());

        // Verify
        verify(funkoService, times(1)).update(anyLong(), any());
    }


    @Test
    void updatePartialFunko() {
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("null")
                .precio(9.99)
                .cantidad(5)
                .imagen("funko3.jpg")
                .categoria("null")
                .build();

        // Arrange
        when(funkoService.update(1L, funkoUpdateDto)).thenReturn(funko1);

        ResponseEntity<Funko> responseEntity = funkoController.updateFunko(1L, funkoUpdateDto);

        // Assert
        assertAll(
                () -> assertEquals(200, responseEntity.getStatusCode().value()),
                () -> assertEquals(funko1, responseEntity.getBody())
        );

        // Verify
        verify(funkoService, times(1)).update(1L, funkoUpdateDto);
    }

    @Test
    void deleteFunkoById() {
        // Arrange
        doNothing().when(funkoService).deleteById(any());

        // Act
        ResponseEntity<Void> responseEntity = funkoController.deleteFunko(any());

        // Assert
        assertAll(() -> assertEquals(204, responseEntity.getStatusCode().value()));

        // Verify
        verify(funkoService, times(1)).deleteById(any());
    }

    @Test
    void deleteFunkoIdFalse() {
        // Arrange
        doThrow(new FunkoNotFound(1L)).when(funkoService).deleteById(any());

        // Act
        var result = assertThrows(FunkoNotFound.class, () -> funkoController.deleteFunko(any()));
        assertEquals("Funko no encontrado con id: " + 1L, result.getMessage());

        // Verify
        verify(funkoService, times(1)).deleteById(any());
    }

}