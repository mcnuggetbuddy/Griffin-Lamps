package com.GriffinLamps.pagina.Controller;

import com.GriffinLamps.pagina.Domain.Coleccion;
import com.GriffinLamps.pagina.Service.ColeccionService;
import jakarta.validation.Valid;
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
@RequestMapping("/colecciones")
public class ColeccionController {

    private final ColeccionService coleccionService;
    private final MessageSource messageSource;

    public ColeccionController(ColeccionService coleccionService, MessageSource messageSource) {
        this.coleccionService = coleccionService;
        this.messageSource = messageSource;
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
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("imagen");
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Coleccion coleccion,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        coleccionService.save(coleccion, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        if (coleccion.getId() != null) {
            return "redirect:/colecciones/modificar/" + coleccion.getId();
        }
        return "redirect:/colecciones/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            coleccionService.delete(id);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "coleccion.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "coleccion.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "coleccion.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/colecciones/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Coleccion> coleccionOpt = coleccionService.getColeccion(id);
        if (coleccionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("coleccion.error01", null, Locale.getDefault()));
            return "redirect:/colecciones/listado";
        }
        model.addAttribute("coleccion", coleccionOpt.get());
        return "/colecciones/modifica";
    }
}