package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.EstadoPedido;
import com.GriffinLamps.pagina.Domain.Pedido;
import com.GriffinLamps.pagina.Repository.PedidoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public Pedido getPedidoConDetalle(Integer idPedido) {
        return pedidoRepository.findByIdPedidoConDetalle(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido con ID " + idPedido + " no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Pedido> getPedidos() {
        return pedidoRepository.findAllOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> getPedido(Integer id) {
        return pedidoRepository.findById(id);
    }

    @Transactional
    public void actualizarEstado(Integer id, EstadoPedido estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido con ID " + id + " no encontrado."));
        pedido.setEstado(estado);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void delete(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("El pedido con ID " + id + " no existe.");
        }
        try {
            pedidoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el pedido. Tiene datos asociados.", e);
        }
    }

    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoPedido estado) {
        return pedidoRepository.countByEstado(estado);
    }
}