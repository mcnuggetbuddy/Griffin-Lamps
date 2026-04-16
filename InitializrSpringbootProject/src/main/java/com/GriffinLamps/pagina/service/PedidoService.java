package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Pedido;
import com.GriffinLamps.pagina.Repository.PedidoRepository;
import java.util.NoSuchElementException;
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
}