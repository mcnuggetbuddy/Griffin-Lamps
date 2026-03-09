package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Coleccion;
import com.GriffinLamps.pagina.Repository.ColeccionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Transactional
    public void save(Coleccion coleccion) {
        coleccionRepository.save(coleccion);
    }

    @Transactional
    public void delete(Integer id) {
        if (!coleccionRepository.existsById(id)) {
            throw new IllegalArgumentException("La colección con ID " + id + " no existe.");
        }
        try {
            coleccionRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la colección. Tiene datos asociados.", e);
        }
    }
}
