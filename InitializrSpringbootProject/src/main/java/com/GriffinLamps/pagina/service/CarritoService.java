package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Color;
import com.GriffinLamps.pagina.Domain.Item;
import com.GriffinLamps.pagina.Domain.Producto;
import com.GriffinLamps.pagina.Domain.Variante;
import com.GriffinLamps.pagina.Domain.DetallePedido;
import com.GriffinLamps.pagina.Domain.EstadoPedido;
import com.GriffinLamps.pagina.Domain.Pedido;
import com.GriffinLamps.pagina.Repository.DetallePedidoRepository;
import com.GriffinLamps.pagina.Repository.PedidoRepository;
import com.GriffinLamps.pagina.Repository.ColorRepository;
import com.GriffinLamps.pagina.Repository.ProductoRepository;
import com.GriffinLamps.pagina.Repository.VarianteRepository;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {

    private static final String ATTRIBUTE_CARRITO = "carrito";

    private final ProductoRepository productoRepository;
    private final ColorRepository colorRepository;
    private final VarianteRepository varianteRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public CarritoService(ProductoRepository productoRepository,
            ColorRepository colorRepository,
            VarianteRepository varianteRepository,
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository) {
        this.productoRepository = productoRepository;
        this.colorRepository = colorRepository;
        this.varianteRepository = varianteRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public List<Item> obtenerCarrito(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Item> carrito = (List<Item>) session.getAttribute(ATTRIBUTE_CARRITO);
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        return carrito;
    }

    public void guardarCarrito(HttpSession session, List<Item> carrito) {
        session.setAttribute(ATTRIBUTE_CARRITO, carrito);
    }

    public void agregarProducto(List<Item> carrito, Integer idProducto, Integer idColor, Integer idVariante) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new RuntimeException("El producto no está disponible.");
        }

        if (producto.getExistencias() == null || producto.getExistencias() <= 0) {
            throw new RuntimeException("Producto agotado.");
        }

        Color color = null;
        if (idColor != null) {
            color = colorRepository.findById(idColor)
                    .orElseThrow(() -> new RuntimeException("Color no encontrado."));
            if (color.getProducto() == null || !color.getProducto().getIdProducto().equals(idProducto)) {
                throw new RuntimeException("El color no pertenece al producto seleccionado.");
            }
        }

        Variante variante = null;
        if (idVariante != null) {
            variante = varianteRepository.findById(idVariante)
                    .orElseThrow(() -> new RuntimeException("Variante no encontrada."));
            if (variante.getProducto() == null || !variante.getProducto().getIdProducto().equals(idProducto)) {
                throw new RuntimeException("La variante no pertenece al producto seleccionado.");
            }
        }

        Optional<Item> itemExistente = carrito.stream()
                .filter(i -> sonElMismoItem(i, idProducto, idColor, idVariante))
                .findFirst();

        if (itemExistente.isPresent()) {
            Item item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + 1;

            if (nuevaCantidad > producto.getExistencias()) {
                throw new RuntimeException("Stock insuficiente para agregar otra unidad.");
            }

            item.setCantidad(nuevaCantidad);
        } else {
            Item nuevoItem = new Item();
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(1);
            nuevoItem.setColor(color);
            nuevoItem.setVariante(variante);
            nuevoItem.setPrecioHistorico(calcularPrecioHistorico(producto, variante));

            if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
                nuevoItem.setRutaImagen(producto.getImagenes().get(0).getRutaImagen());
            }
            carrito.add(nuevoItem);
        }
    }

    public Item buscarItem(List<Item> carrito, int indice) {
        if (carrito == null || indice < 0 || indice >= carrito.size()) {
            return null;
        }
        return carrito.get(indice);
    }

    public void eliminarItem(List<Item> carrito, int indice) {
        if (carrito == null || indice < 0 || indice >= carrito.size()) {
            throw new RuntimeException("El producto no existe en el carrito.");
        }
        carrito.remove(indice);
    }

    public void actualizarCantidad(List<Item> carrito, int indice, int nuevaCantidad) {
        if (carrito == null || indice < 0 || indice >= carrito.size()) {
            throw new RuntimeException("El producto no existe en el carrito.");
        }

        if (nuevaCantidad <= 0) {
            carrito.remove(indice);
            return;
        }

        Item item = carrito.get(indice);
        Producto producto = productoRepository.findById(item.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

        if (nuevaCantidad > producto.getExistencias()) {
            throw new RuntimeException("No hay suficiente stock disponible.");
        }

        item.setCantidad(nuevaCantidad);
    }

    public int contarUnidades(List<Item> carrito) {
        if (carrito == null || carrito.isEmpty()) {
            return 0;
        }

        return carrito.stream()
                .mapToInt(Item::getCantidad)
                .sum();
    }

    public BigDecimal calcularTotal(List<Item> carrito) {
        if (carrito == null || carrito.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return carrito.stream()
                .map(Item::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void limpiarCarrito(HttpSession session) {
        List<Item> carrito = obtenerCarrito(session);
        carrito.clear();
        guardarCarrito(session, carrito);
    }

    private BigDecimal calcularPrecioHistorico(Producto producto, Variante variante) {
        BigDecimal precio = producto.getPrecioColones();

        if (variante != null && variante.getPrecioExtra() != null) {
            precio = precio.add(variante.getPrecioExtra());
        }

        return precio;
    }

    private boolean sonElMismoItem(Item item, Integer idProducto, Integer idColor, Integer idVariante) {
        Integer itemProducto = item.getProducto().getIdProducto();
        Integer itemColor = item.getColor() != null ? item.getColor().getIdColor() : null;
        Integer itemVariante = item.getVariante() != null ? item.getVariante().getIdVariante() : null;

        return iguales(itemProducto, idProducto)
                && iguales(itemColor, idColor)
                && iguales(itemVariante, idVariante);
    }

    private boolean iguales(Integer a, Integer b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    @Transactional
    public Pedido procesarPedido(List<Item> carrito, Pedido pedidoDatos) {

        if (carrito == null || carrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        Pedido pedido = new Pedido();
        pedido.setNumeroOrden(generarNumeroOrden());
        pedido.setNombreCliente(pedidoDatos.getNombreCliente());
        pedido.setCorreo(pedidoDatos.getCorreo());
        pedido.setTelefono(pedidoDatos.getTelefono());
        pedido.setDireccionEnvio(pedidoDatos.getDireccionEnvio());
        pedido.setMetodoPago(pedidoDatos.getMetodoPago());
        pedido.setTotalColones(calcularTotal(carrito));
        pedido.setFecha(LocalDate.now());
        pedido.setEstado(EstadoPedido.Pendiente);

        pedido = pedidoRepository.save(pedido);

        for (Item item : carrito) {
            Producto producto = productoRepository.findById(item.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

            if (item.getCantidad() > producto.getExistencias()) {
                throw new RuntimeException("No hay suficiente stock para el producto: " + producto.getNombre());
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnit(item.getPrecioHistorico());
            detalle.setColor(item.getColor() != null ? item.getColor().getNombre() : null);
            detalle.setTalla(item.getVariante() != null ? item.getVariante().getTamano() : null);

            detallePedidoRepository.save(detalle);

            producto.setExistencias(producto.getExistencias() - item.getCantidad());
            productoRepository.save(producto);
        }

        return pedido;
    }

    private String generarNumeroOrden() {
        return "PED-" + System.currentTimeMillis();
    }
}
