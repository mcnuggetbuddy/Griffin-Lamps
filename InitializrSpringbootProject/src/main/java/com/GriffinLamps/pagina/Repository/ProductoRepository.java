package com.GriffinLamps.pagina.Repository;

import com.GriffinLamps.pagina.Domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}
