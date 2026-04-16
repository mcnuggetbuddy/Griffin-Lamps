package com.GriffinLamps.pagina.Repository;

import com.GriffinLamps.pagina.Domain.Color;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColorRepository extends JpaRepository<Color, Integer>{
    
}
