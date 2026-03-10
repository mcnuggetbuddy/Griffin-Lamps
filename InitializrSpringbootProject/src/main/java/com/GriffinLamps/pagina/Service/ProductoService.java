package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Producto;
import java.util.List;

public interface ProductoService {

    public List<Producto> getProductos();

    public Producto getProducto(Producto producto);

    public void save(Producto producto);

    public void delete(Producto producto);
}
