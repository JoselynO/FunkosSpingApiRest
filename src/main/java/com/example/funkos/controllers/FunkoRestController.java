package com.example.funkos.controllers;

import com.example.funkos.models.Funko;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.services.FunkoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.example.funkos.dto.FunkoCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("${api.version}/funkos")
public class FunkoRestController {
    private final FunkoService funkoService;

    @Autowired
    public FunkoRestController(FunkoService funkoService) {
        this.funkoService = funkoService;
    }

    @GetMapping()
    public ResponseEntity<List<Funko>> getAllFunkos(
            @RequestParam(required = false) String categoria
    ) {
        log.info("Buscando todos los funkos por categoria: " + categoria);
        return ResponseEntity.ok(funkoService.findAll(categoria));
    }

    @GetMapping("/{id}")
        public ResponseEntity<Funko> getFunkoById(@PathVariable Long id) {
            log.info("Buscando Funko por id: " + id);
            return ResponseEntity.ok(funkoService.findById(id));
        }

    @PostMapping()
    public ResponseEntity<Funko> createFunko(@Valid @RequestBody FunkoCreateDto funkoCreateDto) {
            log.info("Creando funko: " + funkoCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(funkoService.save(funkoCreateDto));
        }

    @PutMapping("/{id}")
        public ResponseEntity<Funko> updateFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funkoUpdateDto) {
            log.info("Actualizando funko por id: " + id + " con funko: " + funkoUpdateDto);
            return ResponseEntity.ok(funkoService.update(id, funkoUpdateDto));
        }

    @PatchMapping("/{id}")
    public ResponseEntity<Funko> updatePartialFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funkoUpdateDto) {
        log.info("Actualizando parcialmente funko por id: " + id + " con funko: " + funkoUpdateDto);
        return ResponseEntity.ok(funkoService.update(id, funkoUpdateDto));
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunko(@PathVariable Long id) {
        log.info("Borrando funko por id: " + id);
        funkoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Funko> nuevoFunko(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        log.info("Actualizando imagen de funko por id: " + id);

        if (!file.isEmpty()) {

            return ResponseEntity.ok(funkoService.updateImage(id, file));

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el funko o esta está vacía");
        }
    }
}
