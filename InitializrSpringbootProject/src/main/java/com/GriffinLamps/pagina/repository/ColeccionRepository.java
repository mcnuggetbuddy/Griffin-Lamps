package com.GriffinLamps.pagina.Repository;

import com.GriffinLamps.pagina.Domain.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColeccionRepository extends JpaRepository<Coleccion, Integer> {
}
