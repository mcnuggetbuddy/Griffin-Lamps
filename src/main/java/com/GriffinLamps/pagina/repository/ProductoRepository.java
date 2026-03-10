package com.GriffinLamps.pagina.repository;

import com.GriffinLamps.pagina.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
