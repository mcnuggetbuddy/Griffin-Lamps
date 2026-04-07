package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Coleccion;
import com.GriffinLamps.pagina.Repository.ColeccionRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ColeccionService {

    private final ColeccionRepository coleccionRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ColeccionService(ColeccionRepository coleccionRepository, FirebaseStorageService firebaseStorageService) {
        this.coleccionRepository = coleccionRepository;
        this.firebaseStorageService = firebaseStorageService;
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
    public void save(Coleccion coleccion, MultipartFile imagenFile) {
        Coleccion target;
        if (coleccion.getId() != null) {
            target = coleccionRepository.findById(coleccion.getId()).orElseThrow();
            target.setNombre(coleccion.getNombre());
            if (imagenFile != null && !imagenFile.isEmpty()) {
                if (target.getImagen() != null) {
                    firebaseStorageService.deleteImage(target.getImagen());
                }
                try {
                    target.setImagen(firebaseStorageService.uploadColeccionImage(imagenFile, target.getId()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            target = coleccion;
        }
        coleccionRepository.save(target);
    }

    @Transactional
    public void delete(Integer id) {
        Coleccion coleccion = coleccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La colección con ID " + id + " no existe."));
        if (coleccion.getImagen() != null) {
            firebaseStorageService.deleteImage(coleccion.getImagen());
        }
        try {
            coleccionRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la colección. Tiene datos asociados.", e);
        }
    }
}
