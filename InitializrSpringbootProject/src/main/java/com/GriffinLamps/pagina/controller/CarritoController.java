package com.GriffinLamps.pagina.Controller;

import com.GriffinLamps.pagina.Domain.Item;
import com.GriffinLamps.pagina.Service.CarritoService;
import com.GriffinLamps.pagina.Domain.Pedido;
import com.GriffinLamps.pagina.Service.PedidoService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CarritoController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;

    public CarritoController(CarritoService carritoService, PedidoService pedidoService) {
        this.carritoService = carritoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/carrito/listado")
    public String listado(HttpSession session, Model model) {
        List<Item> carrito = carritoService.obtenerCarrito(session);

        model.addAttribute("carritoItems", carrito);
        model.addAttribute("totalCarrito", carritoService.calcularTotal(carrito));

        return "/carrito/listado";
    }

    @PostMapping("/carrito/agregar")
    public ModelAndView agregar(@RequestParam("idProducto") Integer idProducto,
            @RequestParam(value = "idColor", required = false) Integer idColor,
            @RequestParam(value = "idVariante", required = false) Integer idVariante,
            @RequestParam(value = "cantidad", defaultValue = "1") int cantidad,
            HttpSession session,
            Model model) {
        try {
            List<Item> carrito = carritoService.obtenerCarrito(session);

            carritoService.agregarProducto(carrito, idProducto, idColor, idVariante, cantidad);
            carritoService.guardarCarrito(session, carrito);

            model.addAttribute("carritoTotal", carritoService.calcularTotal(carrito));
            model.addAttribute("listaItems", carrito);

            return new ModelAndView("/carrito/fragmentos :: verCarrito", model.asMap());

        } catch (RuntimeException e) {
            model.addAttribute("errorMensaje", e.getMessage());
            return new ModelAndView("/errores/fragmentos :: errorMensaje", model.asMap());
        }
    }

    @PostMapping("/carrito/eliminar/{indice}")
    public String eliminarItem(@PathVariable("indice") int indice,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            List<Item> carrito = carritoService.obtenerCarrito(session);
            carritoService.eliminarItem(carrito, indice);
            carritoService.guardarCarrito(session, carrito);

            redirectAttributes.addFlashAttribute("todoOk", "Producto eliminado del carrito.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito/listado";
    }

    @GetMapping("/carrito/modificar/{indice}")
    public String modificar(@PathVariable("indice") int indice,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        List<Item> carrito = carritoService.obtenerCarrito(session);
        Item item = carritoService.buscarItem(carrito, indice);

        if (item == null) {
            redirectAttributes.addFlashAttribute("error", "El producto no existe en el carrito.");
            return "redirect:/carrito/listado";
        }

        model.addAttribute("item", item);
        model.addAttribute("indice", indice);

        return "/carrito/modifica";
    }

    @PostMapping("/carrito/actualizar")
    public String actualizarCantidad(@RequestParam("indice") int indice,
            @RequestParam("cantidad") int nuevaCantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            List<Item> carrito = carritoService.obtenerCarrito(session);
            carritoService.actualizarCantidad(carrito, indice, nuevaCantidad);
            carritoService.guardarCarrito(session, carrito);

            redirectAttributes.addFlashAttribute("todoOk", "Cantidad actualizada.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito/listado";
    }

    @GetMapping("/carrito/facturar")
    public String facturarFormulario(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        List<Item> carrito = carritoService.obtenerCarrito(session);
        if (carrito == null || carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/carrito/listado";
        }
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("carritoItems", carrito);
        model.addAttribute("totalCarrito", carritoService.calcularTotal(carrito));
        return "/carrito/facturar";
    }

    @PostMapping("/facturar/carrito")
    public String facturarCarrito(Pedido pedido,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            List<Item> carrito = carritoService.obtenerCarrito(session);
            Pedido pedidoGuardado = carritoService.procesarPedido(carrito, pedido);
            carritoService.limpiarCarrito(session);
            redirectAttributes.addFlashAttribute("idPedido", pedidoGuardado.getId());
            redirectAttributes.addFlashAttribute("todoOk","Pedido realizado con éxito. Número de orden: " + pedidoGuardado.getNumeroOrden());
            return "redirect:/carrito/verPedido";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito/listado";
        }
    }

    @GetMapping("/carrito/verPedido")
    public String verPedido(@ModelAttribute("idPedido") Integer idPedido, Model model) {
        if (idPedido == null) {
            return "redirect:/";
        }
        Pedido pedido = pedidoService.getPedidoConDetalle(idPedido);
        model.addAttribute("pedido", pedido);
        return "/carrito/verPedido";
    }
}
