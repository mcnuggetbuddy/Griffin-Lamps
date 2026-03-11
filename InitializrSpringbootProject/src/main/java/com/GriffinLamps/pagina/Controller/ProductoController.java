package com.GriffinLamps.pagina.Controller;

import com.GriffinLamps.pagina.Domain.Producto;
import com.GriffinLamps.pagina.Service.ProductoService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("")
    public String productoRoot() {
        return "redirect:/producto/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos();
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("producto", new Producto());
        return "/producto/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto, RedirectAttributes redirectAttributes) {
        productoService.save(producto);
        redirectAttributes.addFlashAttribute("todoOk", "Producto guardado correctamente.");
        return "redirect:/producto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoService.delete(id);
            redirectAttributes.addFlashAttribute("todoOk", "Producto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el producto.");
        }
        return "redirect:/producto/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Producto> productoOpt = productoService.getProducto(id);
        if (productoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El producto no existe.");
            return "redirect:/producto/listado";
        }
        model.addAttribute("producto", productoOpt.get());
        return "/producto/modifica";
    }
}
