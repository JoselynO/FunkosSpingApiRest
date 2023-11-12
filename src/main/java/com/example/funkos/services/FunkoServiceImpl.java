package com.example.funkos.services;

import com.example.categoria.models.Categoria;
import com.example.categoria.services.CategoriaService;
import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.exceptions.FunkoNotFound;
import com.example.funkos.mappers.FunkoMapper;
import com.example.funkos.models.Funko;
import com.example.funkos.repositories.FunkosRepository;
import com.example.notifications.config.WebSocketConfig;
import com.example.notifications.config.WebSocketHandler;
import com.example.notifications.dto.FunkoNotificacionDto;
import com.example.notifications.mappers.FunkoNotificationMapper;
import com.example.notifications.models.Notificacion;
import com.example.storage.services.StorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Service
@CacheConfig(cacheNames = {"funkos"})
@Slf4j
public class FunkoServiceImpl implements FunkoService{
    private final FunkosRepository funkosRepository;
    private final CategoriaService categoriaService;
    private final FunkoMapper funkoMapper;
    private final StorageService storageService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final FunkoNotificationMapper funkoNotificationMapper;
    private WebSocketHandler webSocketService;

    @Autowired
    public FunkoServiceImpl(FunkosRepository funkosRepository, CategoriaService categoriaService, FunkoMapper funkoMapper, StorageService storageService, WebSocketConfig webSocketConfig, ObjectMapper mapper, FunkoNotificationMapper funkoNotificationMapper, WebSocketHandler webSocketService) {
        this.funkosRepository = funkosRepository;
        this.categoriaService = categoriaService;
        this.funkoMapper = funkoMapper;
        this.storageService = storageService;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketFunkosHandler();
        this.mapper = new ObjectMapper();
        this.funkoNotificationMapper = funkoNotificationMapper;

    }

    @Override
    public List<Funko> findAll(String categoria) {
        if (categoria == null || categoria.isEmpty()) {
            log.info("Buscando todos los funkos");
            return funkosRepository.findAll();
        }
            log.info("Buscando funkos por categoria: " + categoria);
            return funkosRepository.findByCategoriaContainsIgnoreCase(categoria.toLowerCase());
        }


    @Override
    public Funko findById(Long id) {
        log.info("Buscando funko por id: " + id);
        return funkosRepository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
    }

    @Override
    public Funko save(FunkoCreateDto funkoCreateDto) {
        log.info("Guardando funko: " + funkoCreateDto);

        var categoria = categoriaService.findByNombre(funkoCreateDto.getCategoria());

        var funkoSaved = funkosRepository.save(funkoMapper.toFunko(funkoCreateDto, categoria));

        onChange(Notificacion.Tipo.CREATE, funkoSaved);
        return funkoSaved;
    }

    @Override
    public Funko update(Long id, FunkoUpdateDto funkoUpdateDto) {
        log.info("Actualizando funko por id: " + id);

        var funkoActual = this.findById(id);

        Categoria categoria = null;
        if (funkoUpdateDto.getCategoria() != null && !funkoUpdateDto.getCategoria().isEmpty()) {
            categoria = categoriaService.findByNombre(funkoUpdateDto.getCategoria());
        } else {
            categoria = funkoActual.getCategoria();
        }

        var funkoUpdated = funkosRepository.save(funkoMapper.toFunko(funkoUpdateDto, funkoActual, categoria));

        onChange(Notificacion.Tipo.UPDATE, funkoUpdated);
        return funkoUpdated;
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Borrando funko por id: " + id);

        var funk = this.findById(id);
        funkosRepository.deleteById(id);
        if (funk.getImagen() != null && !funk.getImagen().equals(Funko.IMAGE_DEFAULT)) {
            storageService.delete(funk.getImagen());
        }
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.DELETE, funk);
    }

    @Override
    @CachePut
    public Funko updateImage(Long id, MultipartFile image) {
        log.info("Actualizando imagen de funko por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        var funkoActual = this.findById(id);
        // Borramos la imagen anterior si existe y no es la de por defecto
        if (funkoActual.getImagen() != null && !funkoActual.getImagen().equals(Funko.IMAGE_DEFAULT)) {
            storageService.delete(funkoActual.getImagen());
        }
        String imageStored = storageService.store(image);
        String imageUrl = storageService.getUrl(imageStored);
        var funkoActualizado = new Funko(
                funkoActual.getId(),
                funkoActual.getNombre(),
                funkoActual.getPrecio(),
                funkoActual.getCantidad(),
                imageUrl,
                funkoActual.getFechaDeCreacion(),
                funkoActual.getFechaDeActualizacion(),
                funkoActual.getActivo(),
                funkoActual.getCategoria()
        );
        var funkoUp = funkosRepository.save(funkoActualizado);

        onChange(Notificacion.Tipo.UPDATE,  funkoUp);
        return  funkoUp;
    }

    void onChange(Notificacion.Tipo tipo, Funko data) {
        log.debug("Servicio de funkos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {
            Notificacion<FunkoNotificacionDto> notificacion = new Notificacion<>(
                    "FUNKOS",
                    tipo,
                    funkoNotificationMapper.toFunkoNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");

            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    // Para los test
    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}


