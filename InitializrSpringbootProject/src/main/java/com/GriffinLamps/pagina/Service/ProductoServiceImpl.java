package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Producto;
import com.GriffinLamps.pagina.Repository.ProductoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto getProducto(Producto producto) {
        return productoRepository.findById(producto.getIdProducto()).orElse(null);
    }

    @Override
    public void save(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public void delete(Producto producto) {
        productoRepository.delete(producto);
    }
}
