package com.example.rest.funkos.services;

import com.example.categoria.models.Categoria;
import com.example.categoria.services.CategoriaService;
import com.example.config.websockets.WebSocketConfig;
import com.example.config.websockets.WebSocketHandler;
import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.exceptions.FunkoNotFound;
import com.example.funkos.mappers.FunkoMapper;
import com.example.funkos.models.Funko;
import com.example.funkos.repositories.FunkosRepository;
import com.example.funkos.services.FunkoServiceImpl;
import com.example.storage.services.StorageService;
import com.example.websockets.notifications.mappers.FunkoNotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FunkoServiceTest {
    private final Categoria categoria = new Categoria(1L, "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko1 = new Funko(1l, "TEST-1", 10.99,5, "test1.png", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    private final Funko funko2 = new Funko(2l, "TEST-2", 11.99,6, "test2.png", LocalDateTime.now(), LocalDateTime.now(), true, categoria);
    WebSocketHandler webSocketHandlerMock = mock (WebSocketHandler.class);

    @Mock
    private FunkosRepository funkoRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private CategoriaService categoriaService;
    @Mock
    private FunkoMapper funkoMapper;
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private FunkoNotificationMapper funkoNotificationMapper;
    @InjectMocks
    private FunkoServiceImpl funkoService;

    @BeforeEach
    void setUp(){
        funkoService.setWebSocketService(webSocketHandlerMock);
    }

    @Test
    void findAll(){
        // Arrange
        List<Funko> funkoList = List.of(funko1, funko2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> page = new PageImpl<>(funkoList);

        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Funko> actualPage = funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(page, actualPage)
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findAllNombre(){
        // Arrange
        Optional<String> nombre = Optional.of("TEST-2");
        List<Funko> funkoList = List.of(funko2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> page = new PageImpl<>(funkoList);

        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Funko> actualPage = funkoService.findAll(nombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(page, actualPage)
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findAllCategoria(){
        // Arrange
        Optional<String> categoria = Optional.of("DISNEY");
        List<Funko> funkoList = List.of(funko1, funko2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> page = new PageImpl<>(funkoList);

        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Funko> actualPage = funkoService.findAll(Optional.empty(), categoria, Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(page, actualPage)
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findAllPrecio(){
        // Arrange
        Optional<Double> precioMax = Optional.of(11.99);
        List<Funko> funkoList = List.of(funko1, funko2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> page = new PageImpl<>(funkoList);

        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Funko> actualPage = funkoService.findAll(Optional.empty(), Optional.empty(), precioMax, Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(page, actualPage)
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findAllActivo(){
        // Arrange
        Optional<Boolean> activo = Optional.of(true);
        List<Funko> funkoList = List.of(funko1, funko2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> page = new PageImpl<>(funkoList);

        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Funko> actualPage = funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), activo, pageable);

        // Assert
        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(page, actualPage)
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findById(){
        // Arrange
        Long id = 2L;

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko2));

        // Act
        Funko funkoFind = funkoService.findById(id);

        // Assert
        assertAll(
                () -> assertNotNull(funkoFind),
                () -> assertEquals(funko2, funkoFind),
                () -> assertEquals(funko2.getId(), funkoFind.getId()),
                () -> assertEquals(funko2.getPrecio(), funkoFind.getPrecio()),
                () -> assertEquals(funko2.getImagen(), funkoFind.getImagen()),
                () -> assertEquals(funko2.getCantidad(), funkoFind.getCantidad()),
                () -> assertEquals(funko2.getFechaDeCreacion(), funkoFind.getFechaDeCreacion()),
                () -> assertEquals(funko2.getActivo(), funkoFind.getActivo()),
                () -> assertEquals(funko2.getCategoria(), funkoFind.getCategoria()),
                () -> assertEquals(funko2.getFechaDeActualizacion(), funkoFind.getFechaDeActualizacion()),
                () -> assertEquals(funko2.getNombre(), funkoFind.getNombre())
        );

        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void findAllCantidad(){
        // Arrange
        Optional<Integer> cantidadMin = Optional.of(5);
        List<Funko> funkoList = List.of(funko1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> page = new PageImpl<>(funkoList);

        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Funko> actualPage = funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), cantidadMin, Optional.empty(), pageable);

        // Assert
        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(page, actualPage)
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findByIdNotExist(){
        // Arrange
        Long id = 20L;

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(FunkoNotFound.class, () -> funkoService.findById(id));
        assertEquals("Funko no encontrado con id: " + id, res.getMessage());

        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void save() throws IOException {
        // Arrange
        FunkoCreateDto funkoCreateDto = FunkoCreateDto.builder()
                .nombre("FunkoSave")
                .precio(2.99)
                .imagen("funkosave.jpg")
                .categoria("DISNEY")
                .cantidad(2)
                .build();
        Funko funko = funko2;

        when(funkoRepository.save(funko)).thenReturn(funko);
        when(categoriaService.findByNombre(funkoCreateDto.getCategoria())).thenReturn(categoria);
        when(funkoMapper.toFunko(funkoCreateDto, categoria)).thenReturn(funko);
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        Funko savedFunko = funkoService.save(funkoCreateDto);

        // Assert
        assertAll(
                () -> assertNotNull(savedFunko),
                () -> assertEquals(funko, savedFunko)
        );

        verify(funkoRepository, times(1)).save(funko);
        verify(funkoMapper, times(1)).toFunko(funkoCreateDto, categoria);
        verify(categoriaService, times(1)).findByNombre(funkoCreateDto.getCategoria());
    }

   /* @Test
    void saveCategoryNotExist(){
        // Arrange
        FunkoCreateDto funkoCreateDto = FunkoCreateDto.builder()
                .nombre("FunkoSave")
                .precio(2.99)
                .imagen("funkosave.jpg")
                .categoria("DISNEY")
                .cantidad(2)
                .build();

        when(categoriaService.findByNombre(funkoCreateDto.getCategoria())).thenThrow(new CategoriaNotFound(funkoCreateDto.getCategoria()));

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> funkoService.save(funkoCreateDto));
        assertEquals("Categoria con nombre" + categoria  + "no encontrada", res.getMessage());

        verify(categoriaService, times(1)).findByNombre(funkoCreateDto.getCategoria());
    }*/

    @Test
    void update() throws IOException {
        // Arrange
        Long id = 1L;
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoUpdate")
                .precio(7.99)
                .cantidad(50)
                .categoria("PELICULAS")
                .imagen("funkoupdate.jpg")
                .build();

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko1));
        when(funkoRepository.save(funko1)).thenReturn(funko1);
        when(funkoMapper.toFunko(funkoUpdateDto, funko1, categoria)).thenReturn(funko1);
        when(categoriaService.findByNombre(funkoUpdateDto.getCategoria())).thenReturn(categoria);
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        Funko funkoActualizado = funkoService.update(id, funkoUpdateDto);

        // Assert
        assertAll(
                () -> assertNotNull(funkoActualizado),
                () -> assertEquals(funko1, funkoActualizado)
        );

        verify(funkoRepository, times(1)).findById(id);
        verify(funkoRepository, times(1)).save(funko1);
        verify(categoriaService, times(1)).findByNombre(funkoUpdateDto.getCategoria());
        verify(funkoMapper, times(1)).toFunko(funkoUpdateDto, funko1, categoria);
    }

    @Test
    void updateIdNoExist(){
        // Arrange
        Long id = 99L;
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoUpdate")
                .precio(7.99)
                .cantidad(50)
                .categoria("PELICULAS")
                .imagen("funkoupdate.jpg")
                .build();

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(FunkoNotFound.class, () -> funkoService.update(id, funkoUpdateDto));
        assertEquals("Funko no encontrado con id: " + id, res.getMessage());

        verify(funkoRepository, times(1)).findById(id);
        verify(funkoRepository, times(0)).save(any(Funko.class));
    }


    /*@Test
    void updateCategoryFalse(){
        // Arrange
        Long id = 1L;
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre("FunkoUpdate")
                .precio(7.99)
                .cantidad(50)
                .categoria("PELICULAS")
                .imagen("funkoupdate.jpg")
                .build();

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko1));
        when(categoriaService.findByNombre(funkoUpdateDto.getCategoria())).thenThrow(new CategoriaNotFound(funkoUpdateDto.getCategoria()));

        // Act
        var res = assertThrows(CategoriaNotFound.class, () -> funkoService.update(id, funkoUpdateDto));
        assertEquals("Categoria con nombre " + categoria + " no encontrada", res.getMessage());

        verify(funkoRepository, times(1)).findById(id);
        verify(categoriaService, times(1)).findByNombre(funkoUpdateDto.getCategoria());
        verify(funkoRepository, times(0)).save(any(Funko.class));
    }*/

    @Test
    void deleteById() throws IOException {
        // Arrange
        Long id = 1L;

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko1));
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        funkoService.deleteById(id);

        verify(funkoRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteByIdNotExist(){
        // Arrange
        Long id = 99L;

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var res = assertThrows(FunkoNotFound.class, () -> funkoService.deleteById(id));
        assertEquals("Funko no encontrado con id: " + id, res.getMessage());

        verify(funkoRepository, times(1)).findById(id);
        verify(funkoRepository, times(0)).deleteById(id);
    }

    @Test
    void updateImage() throws IOException {
        // Arrange
        String imageUrl = "test1.png";

        MultipartFile multipartFile = mock(MultipartFile.class);

        when(funkoRepository.findById(funko1.getId())).thenReturn(Optional.of(funko1));
        when(storageService.store(multipartFile)).thenReturn(imageUrl);
        when(funkoRepository.save(any(Funko.class))).thenReturn(funko1);
        doNothing().when(webSocketHandlerMock).sendMessage(anyString());

        // Act
        Funko updatedFunko = funkoService.updateImage(funko1.getId(), multipartFile);

        // Assert
        assertEquals(updatedFunko.getImagen(), imageUrl);
        verify(funkoRepository, times(1)).save(any(Funko.class));
        verify(storageService, times(1)).delete(funko1.getImagen());
        verify(storageService, times(1)).store(multipartFile);
    }
}



