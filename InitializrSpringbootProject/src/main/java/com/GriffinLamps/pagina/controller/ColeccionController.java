package com.GriffinLamps.pagina.controller;

import com.GriffinLamps.pagina.domain.Coleccion;
import com.GriffinLamps.pagina.service.ColeccionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/colecciones")
public class ColeccionController {

    private final ColeccionService coleccionService;

    public ColeccionController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
    }

    @GetMapping("")
    public String coleccionRoot() {
        return "redirect:/colecciones/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var colecciones = coleccionService.getColecciones();
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("totalColecciones", colecciones.size());
        return "/colecciones/listado";
    }
}