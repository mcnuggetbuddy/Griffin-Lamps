package com.GriffinLamps.pagina.repository;

import com.GriffinLamps.pagina.domain.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColeccionRepository extends JpaRepository<Coleccion, Integer> {
}
