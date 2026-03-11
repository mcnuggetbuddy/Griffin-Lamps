package com.GriffinLamps.pagina.Controller;

import com.GriffinLamps.pagina.Service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ConsultaController {
    
    private final ProductoService productoService;

    public ConsultaController(ProductoService productoService) {
        this.productoService = productoService;
    }
    
    @GetMapping("/")
    public String inicio(Model model) {

        model.addAttribute("productosDestacados",
                productoService.getProductosD(true));

        return "index";
    }
    
    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductosA(false);
        model.addAttribute("productos", productos);
        return "/consultas/listado";
    }
    
}
