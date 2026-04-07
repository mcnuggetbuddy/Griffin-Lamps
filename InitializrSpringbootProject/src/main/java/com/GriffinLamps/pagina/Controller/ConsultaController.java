package com.GriffinLamps.pagina.Controller;

import com.GriffinLamps.pagina.Service.ColeccionService;
import com.GriffinLamps.pagina.Service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ConsultaController {

    private final ProductoService productoService;
    private final ColeccionService coleccionService;

    public ConsultaController(ProductoService productoService, ColeccionService coleccionService) {
        this.productoService = productoService;
        this.coleccionService = coleccionService;
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
            @RequestParam(value = "orden", defaultValue = "nuevo") String orden,
            @RequestParam(value = "buscar", defaultValue = "") String buscar) {
        var productos = buscar.isBlank()
                ? productoService.getProductosOrdenados(orden)
                : productoService.buscarProductos(buscar);
        model.addAttribute("productos", productos);
        model.addAttribute("orden", orden);
        model.addAttribute("buscar", buscar);
        return "cliente/productos";
    }

    @GetMapping("/cliente/detalle/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        var producto = productoService.getProducto(id);
        if (producto.isEmpty()) {
            return "redirect:/cliente/productos";
        }
        model.addAttribute("producto", producto.get());
        return "cliente/detalle";
    }

    @GetMapping("/cliente/colecciones")
    public String colecciones(Model model) {
        model.addAttribute("colecciones", coleccionService.getColecciones());
        return "cliente/colecciones";
    }

    @GetMapping("/cliente/colecciones/{idColeccion}")
    public String coleccionDetalle(@PathVariable Integer idColeccion, Model model) {
        var coleccionOpt = coleccionService.getColeccion(idColeccion);
        if (coleccionOpt.isEmpty()) {
            return "redirect:/cliente/colecciones";
        }
        model.addAttribute("coleccion", coleccionOpt.get());
        model.addAttribute("productos", productoService.getProductosActivosPorColeccion(idColeccion));
        return "cliente/coleccion-detalle";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "about/listado";
    }

    @GetMapping("/acceso_denegado")
    public String accesoDenegado() {
        return "acceso_denegado";
    }
}
