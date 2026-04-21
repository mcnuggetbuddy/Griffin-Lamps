package com.GriffinLamps.pagina.Repository;

import com.GriffinLamps.pagina.Domain.EstadoPedido;
import com.GriffinLamps.pagina.Domain.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @Query("SELECT p FROM Pedido p " +
           "LEFT JOIN FETCH p.detalles d " +
           "LEFT JOIN FETCH d.producto pr " +
           "WHERE p.id = :idPedido")
    Optional<Pedido> findByIdPedidoConDetalle(@Param("idPedido") Integer idPedido);

    @Query("SELECT p FROM Pedido p ORDER BY p.fecha DESC, p.id DESC")
    List<Pedido> findAllOrderByFechaDesc();

    long countByEstado(EstadoPedido estado);
}
