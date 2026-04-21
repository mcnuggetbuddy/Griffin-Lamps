package com.GriffinLamps.pagina.Controller;

import com.GriffinLamps.pagina.Domain.EstadoPedido;
import com.GriffinLamps.pagina.Domain.Pedido;
import com.GriffinLamps.pagina.Service.PedidoService;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ordenes")
public class OrdenesController {

    private final PedidoService pedidoService;
    private final MessageSource messageSource;

    public OrdenesController(PedidoService pedidoService, MessageSource messageSource) {
        this.pedidoService = pedidoService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        List<Pedido> pedidos = pedidoService.getPedidos();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalPedidos", pedidos.size());
        model.addAttribute("totalPendientes", pedidoService.contarPorEstado(EstadoPedido.Pendiente));
        model.addAttribute("totalPagados", pedidoService.contarPorEstado(EstadoPedido.Pagado));
        return "/ordenes/listado";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.getPedidoConDetalle(id);
            model.addAttribute("pedido", pedido);
            model.addAttribute("estados", EstadoPedido.values());
            return "/ordenes/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("orden.error01", null, Locale.getDefault()));
            return "redirect:/ordenes/listado";
        }
    }

    @PostMapping("/actualizarEstado")
    public String actualizarEstado(@RequestParam Integer id,
            @RequestParam EstadoPedido estado,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.actualizarEstado(id, estado);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ordenes/detalle/" + id;
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.delete(id);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.eliminado", null, Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("orden.error01", null, Locale.getDefault()));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("orden.error02", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("orden.error03", null, Locale.getDefault()));
        }
        return "redirect:/ordenes/listado";
    }
}
