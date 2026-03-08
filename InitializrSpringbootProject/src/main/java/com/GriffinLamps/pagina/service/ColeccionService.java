package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Coleccion;
import com.GriffinLamps.pagina.Repository.ColeccionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ColeccionService {

    private final ColeccionRepository coleccionRepository;

    public ColeccionService(ColeccionRepository coleccionRepository) {
        this.coleccionRepository = coleccionRepository;
    }

    @Transactional(readOnly = true)
    public List<Coleccion> getColecciones() {
        return coleccionRepository.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public Optional<Coleccion> getColeccion(Integer id) {
        return coleccionRepository.findById(id);
    }
}
