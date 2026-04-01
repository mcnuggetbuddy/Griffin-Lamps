package com.GriffinLamps.pagina.Controller;

import com.GriffinLamps.pagina.Service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    
    @GetMapping("/cliente/productos")
    public String productos(Model model,
            @RequestParam(value = "orden", defaultValue = "nuevo") String orden) {
        var productos = productoService.getProductosOrdenados(orden);
        model.addAttribute("productos", productos);
        model.addAttribute("orden", orden);
        return "cliente/productos";
    }

    @GetMapping("/cliente/colecciones")
    public String colecciones(Model model) {
        // cuando tengas ColeccionService
        return "cliente/colecciones";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "about/listado";
    }
}
