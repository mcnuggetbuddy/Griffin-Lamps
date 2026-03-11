package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Producto;
import com.GriffinLamps.pagina.Repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos() {
        return productoRepository.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public void save(Producto producto) {
        productoRepository.save(producto);
    }

    @Transactional
    public void delete(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("El producto no existe.");
        }
        try {
            productoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el producto.", e);
        }
    }
}
