/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.GriffinLamps.pagina.Controller;

import com.GriffinLamps.pagina.Domain.Color;
import com.GriffinLamps.pagina.Domain.Producto;
import com.GriffinLamps.pagina.Domain.Variante;
import com.GriffinLamps.pagina.Service.ColeccionService;
import com.GriffinLamps.pagina.Service.ProductoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/producto")
public class ProductoController {
    private final ProductoService productoService;
    private final ColeccionService coleccionService;
    private final MessageSource messageSource;

    public ProductoController(ProductoService productoService, ColeccionService coleccionService, MessageSource messageSource) {
        this.productoService = productoService;
        this.coleccionService = coleccionService;
        this.messageSource = messageSource;
    }
    
    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductosA(false);
        model.addAttribute("productos", productos);
        var colecciones = coleccionService.getColecciones();
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("totalProductos", productos.size());
        return "/producto/listado";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("imagenes");
    }
    
    @PostMapping("/guardar")
    public String guardar(
            @Valid Producto producto,
            @RequestParam(value = "imagenesFile", required = false) MultipartFile[] imagenes,
            RedirectAttributes redirectAttributes) {

        productoService.save(producto, imagenes);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));

        if (producto.getIdProducto() != null) {
            return "redirect:/producto/modificar/" + producto.getIdProducto();
        }
        return "redirect:/producto/listado";
    }
    
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idProducto, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            productoService.delete(idProducto);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "producto.error01";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "producto.error02";
        } catch (Exception e) {
            titulo = "error";  // Captura cualquier otra excepción inesperada
            detalle = "producto.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/producto/listado";
    }
    
    @GetMapping("/modificar/{idProducto}")
    public String modificar(@PathVariable("idProducto") Integer idProducto, Model model, RedirectAttributes redirectAttributes) {
        Optional<Producto> productoOpt = productoService.getProducto(idProducto);
        if (productoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("producto.error01", null, Locale.getDefault()));
            return "redirect:/producto/listado";
        }
        model.addAttribute("producto", productoOpt.get());
        var colecciones = coleccionService.getColecciones();
        model.addAttribute("colecciones", colecciones);
        return "/producto/modifica";
    }
    
    @GetMapping("/eliminarImagen/{idImagen}")
    public String eliminarImagen(@PathVariable Integer idImagen,
            @RequestParam Integer idProducto,
            RedirectAttributes redirect) {

        productoService.deleteImagen(idImagen);
        redirect.addFlashAttribute("mensaje", "Imagen eliminada");
        return "redirect:/producto/modificar/" + idProducto;
    }
    
    @GetMapping("/agregarVariante/{idProducto}")
    public String agregarVariante(@PathVariable Integer idProducto) {
        Variante v = new Variante();
        v.setTamano("Nueva variante");
        v.setPrecioExtra(BigDecimal.ZERO);
        productoService.agregarVariante(idProducto, v);
        return "redirect:/producto/modificar/" + idProducto;
    }
    
    @GetMapping("/eliminarVariante/{idVariante}")
    public String eliminarVariante(@PathVariable Integer idVariante,
            @RequestParam Integer idProducto) {

        productoService.eliminarVariante(idVariante);

        return "redirect:/producto/modificar/" + idProducto;
    }
    
    @GetMapping("/agregarColor/{idProducto}")
    public String agregarColor(@PathVariable Integer idProducto) {
        Color c = new Color();
        c.setNombre("Nuevo color");
        c.setCodigoHex("#000000");
        productoService.agregarColor(idProducto, c);
        return "redirect:/producto/modificar/" + idProducto;
    }
    
    @GetMapping("/eliminarColor/{idColor}")
    public String eliminarColor(@PathVariable Integer idColor,
            @RequestParam Integer idProducto) {

        productoService.eliminarColor(idColor);

        return "redirect:/producto/modificar/" + idProducto;
    }
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {

        Producto producto = productoService.getProducto(id).orElseThrow();

        model.addAttribute("producto", producto);
        model.addAttribute("colecciones", coleccionService.getColecciones());

        return "/producto/modifica";
    }
    
    @PostMapping("/guardarColor")
    public String guardarColor(@RequestParam Integer idColor,
            @RequestParam String nombre,
            @RequestParam String codigoHex,
            @RequestParam Integer idProducto,
            RedirectAttributes redirect) {
        productoService.actualizarColor(idColor, nombre, codigoHex);
        return "redirect:/producto/modificar/" + idProducto;
    }

    @PostMapping("/guardarVariante")
    public String guardarVariante(@RequestParam Integer idVariante,
            @RequestParam String tamano,
            @RequestParam BigDecimal precioExtra,
            @RequestParam Integer idProducto,
            RedirectAttributes redirect) {
        productoService.actualizarVariante(idVariante, tamano, precioExtra);
        return "redirect:/producto/modificar/" + idProducto;
    }
}
