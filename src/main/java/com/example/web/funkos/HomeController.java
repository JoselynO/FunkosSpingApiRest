package com.example.web.funkos;

import com.example.categoria.models.Categoria;
import com.example.categoria.repositories.CategoriaRepository;
import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.models.Funko;
import com.example.funkos.services.FunkoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class HomeController {
    private final FunkoService funkoService;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public HomeController(FunkoService funkoService, CategoriaRepository categoriaRepository) {
        this.funkoService = funkoService;
        this.categoriaRepository = categoriaRepository;
    }
    @GetMapping(path = {"/","/funkos"})
    public String listFunkos(Model model,
                             @RequestParam(value = "nombre", required = false) Optional<String> nombre,
                             @RequestParam(value = "precio", required = false) Optional<Double> precio,
                             @RequestParam(value = "cantidad", required = false) Optional<Integer> cantidad,
                             @RequestParam(value = "activo", required = false) Optional<Boolean> activo,
                             @RequestParam(value = "categoria", required = false) Optional<String> categoria,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "4") int size,
                             @RequestParam(defaultValue = "id") String sortBy,
                             @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Funko> funkosPage = funkoService.findAll(nombre,categoria,precio,cantidad,activo, pageable);
        model.addAttribute("nombre", nombre.orElse(""));
        model.addAttribute("funkosPage",funkosPage);
        return "index";
    }

    @GetMapping("/funkos/create")
    public String createFunkoForm(Model model){
        List<String> categorias = categoriaRepository.findAll().stream().map(Categoria::getNombre).toList();
        model.addAttribute("categorias", categorias);
        model.addAttribute("funko", FunkoCreateDto.builder().build());
        return "funkos/create";
    }

    @PostMapping("/funkos/create")
    public String createFunko(Model model, @Valid @ModelAttribute("funko") FunkoCreateDto funko, BindingResult result){
        if (result.hasErrors()){
            List<String> categorias = categoriaRepository.findAll().stream().map(Categoria::getNombre).toList();
            model.addAttribute("categorias", categorias);
            return "funkos/create";
        }
        funkoService.save(funko);
        return "redirect:/";
    }


    @GetMapping("/funkos/update/{id}")
    public String editFunko(Model model, @PathVariable Long id){
        Funko funko = funkoService.findById(id);
        List<String> categorias = categoriaRepository.findAll().stream().map(Categoria::getNombre).toList();
        model.addAttribute("funko", funko);
        model.addAttribute("categorias",categorias);
        return "funkos/update";
    }

    @PostMapping("/funkos/update/{id}")
    public String updateFunko(@PathVariable Long id,Model model, @Valid @ModelAttribute("funko") FunkoUpdateDto funko, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            List<Categoria> categorias = categoriaRepository.findAll();
            Funko fk = funkoService.findById(id);
            model.addAttribute("funko", fk);
            model.addAttribute("categorias", categorias);
            return "funkos/update";
        }
        funkoService.update(id,funko);
        return "redirect:/funkos/gestion";
    }

    @GetMapping("/funkos/updateImg/{id}")
    public String updateImgFunko(Model model, @PathVariable Long id){
        Funko funko = funkoService.findById(id);
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("funko", funko);
        model.addAttribute("categorias",categorias);
        return "funkos/update-imagine";
    }

    @PostMapping("/funkos/updateImg/{id}")
    public String updatePathImgFunko(@PathVariable Long id,@RequestParam("imagen") MultipartFile imagen){
        funkoService.updateImage(id, imagen);
        return "redirect:/";
    }

    @GetMapping("/funkos/gestion")
    public String gestionFunkos(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Funko> funkosPage = funkoService.findAll(Optional.empty(), Optional.empty(),Optional.empty(),Optional.empty() ,Optional.empty(), pageable);
        model.addAttribute("funkosPage",funkosPage);
        return "funkos/gestion";
    }

    @GetMapping("/funkos/delete/{id}")
    public String deleteFunko(@PathVariable Long id){
        funkoService.findById(id);
        funkoService.deleteById(id);
        return "redirect:/funkos/gestion";
    }
}
