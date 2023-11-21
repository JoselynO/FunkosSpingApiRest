package com.example.web.funkos.controllers;

import com.example.categoria.models.Categoria;
import com.example.categoria.services.CategoriaService;
import com.example.funkos.dto.FunkoCreateDto;
import com.example.funkos.dto.FunkoUpdateDto;
import com.example.funkos.models.Funko;
import com.example.funkos.services.FunkoService;
import com.example.web.funkos.store.UserStore;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/funkos")
@Slf4j
public class FunkosWebController {
    private final FunkoService funkoService;
    private final CategoriaService categoriaService;
    private final MessageSource messageSource;
    private final UserStore userSession;

    @Autowired
    public FunkosWebController(FunkoService funkoService, CategoriaService categoriaService, MessageSource messageSource, UserStore userSession) {
        this.funkoService = funkoService;
        this.categoriaService = categoriaService;
        this.messageSource = messageSource;
        this.userSession = userSession;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        log.info("Login GET");
        if (isLoggedAndSessionIsActive(session)) {
            log.info("Si está logueado volvemos al index");
            return "redirect:/funko";
        }
        return "funko/login";
    }

    @PostMapping
    public String login(@RequestParam("password") String password, HttpSession session, Model model) {
        log.info("Login POST");
        if ("pass".equals(password)) {
            userSession.setLastLogin(new Date());
            userSession.setLogged(true);
            session.setAttribute("userSession", userSession);
            session.setMaxInactiveInterval(1800);
            return "redirect:/funko";
        } else {
            return "funko/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Logout GET");
        session.invalidate();
        return "redirect:/funko";
    }
    @GetMapping(path = {"", "/", "/index", "/list"})
    public String index(HttpSession session,
                        Model model,
                        @RequestParam(value = "search", required = false) Optional<String> search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction
                        ){
        if (!isLoggedAndSessionIsActive(session)) {
        log.info("No hay sesión o no está logueado volvemos al login");
        return "redirect:/funkos/login";
    }

        log.info("Index GET con parámetros search: " + search + ", page: " + page + ", size: " + size);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        var funkosPage = funkoService.findAll(search, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);


        String welcomeMessage = messageSource.getMessage("welcome.message", null, locale);

        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        sessionData.incrementLoginCount();
        var numVisitas = sessionData.getLoginCount();
        var lastLogin = sessionData.getLastLogin();
        var localizedLastLoginDate = getLocalizedDate(lastLogin, locale);

        model.addAttribute("funkosPage", funkosPage);
        model.addAttribute("search", search.orElse(""));
        model.addAttribute("welcomeMessage", welcomeMessage);
        model.addAttribute("numVisitas", numVisitas);
        model.addAttribute("lastLoginDate", localizedLastLoginDate);
        return "productos/index";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, Model model, HttpSession session) {
        log.info("Details GET");

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        Funko funko = funkoService.findById(id);
        model.addAttribute("funko", funko);
        return "funko/details";
    }

    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        log.info("Create GET");


        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funko/login";
        }

        var categorias = categoriaService.findAll(Optional.empty(), PageRequest.of(0, 1000))
                .get()
                .map(Categoria::getNombre);
        var funko = FunkoCreateDto.builder()
                .imagen("https://via.placeholder.com/150")
                .precio(0.0)
                .cantidad(0)
                .build();
        model.addAttribute("funko", funko);
        model.addAttribute("categorias", categorias);
        return "funko/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("funko") FunkoCreateDto funkoDto,
                         BindingResult result,
                         Model model) {
        log.info("Create POST");
        if (result.hasErrors()) {
            var categorias = categoriaService.findAll(Optional.empty(), PageRequest.of(0, 1000))
                    .get()
                    .map(Categoria::getNombre);
            model.addAttribute("categorias", categorias);
            return "funko/create";
        }

        var funko = funkoService.save(funkoDto);
        return "redirect:/funko";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model, HttpSession session) {

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funko/login";
        }

        var categorias = categoriaService.findAll(Optional.empty(), PageRequest.of(0, 1000))
                .get()
                .map(Categoria::getNombre);
        Funko funko = funkoService.findById(id);
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre(funko.getNombre())
                .precio(funko.getPrecio())
                .cantidad(funko.getCantidad())
                .imagen(funko.getImagen())
                .categoria(funko.getCategoria().getNombre())
                .activo(funko.getActivo())
                .build();
        model.addAttribute("funko", funkoUpdateDto);
        model.addAttribute("categorias", categorias);
        return "funko/update";
    }

    @PostMapping("/update/{id}")
    public String updateFunko(@PathVariable("id") Long id, @ModelAttribute FunkoUpdateDto funkoUpdateDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            var categorias = categoriaService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                    .get()
                    .map(Categoria::getNombre);
            model.addAttribute("categorias", categorias);
            return "funko/update";
        }
        log.info("Update POST");
        System.out.println(id);
        System.out.println(funkoUpdateDto);
        var res = funkoService.update(id, funkoUpdateDto);
        System.out.println(res);
        return "redirect:/funko";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session) {

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funko/login";
        }

        funkoService.deleteById(id);
        return "redirect:/funko";
    }

    @GetMapping("/update-image/{id}")
    public String showUpdateImageForm(@PathVariable("id") Long id, Model model, HttpSession session) {

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funko/login";
        }

        Funko funko = funkoService.findById(id);
        model.addAttribute("funko", funko);
        return "funko/update-image";
    }

    @PostMapping("/update-image/{id}")
    public String updateFunkoImage(@PathVariable("id") Long id, @RequestParam("imagen") MultipartFile imagen) {
        log.info("Update POST con imagen");
        funkoService.updateImage(id, imagen);
        return "redirect:/funko";
    }

    private String getLocalizedDate(Date date, Locale locale) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(locale);
        return localDateTime.format(formatter);
    }

    private boolean isLoggedAndSessionIsActive(HttpSession session) {
        log.info("Comprobando si está logueado");
        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        return sessionData != null && sessionData.isLogged();
    }

}
