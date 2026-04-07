/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.GriffinLamps.pagina.Service;

import com.GriffinLamps.pagina.Domain.Color;
import com.GriffinLamps.pagina.Domain.Producto;
import com.GriffinLamps.pagina.Domain.ProductoImagen;
import com.GriffinLamps.pagina.Domain.Variante;
import com.GriffinLamps.pagina.Repository.ProductoRepository;
import com.GriffinLamps.pagina.Repository.ProductoImagenRepository;
import com.GriffinLamps.pagina.Repository.VarianteRepository;
import com.GriffinLamps.pagina.Repository.ColorRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {
    
    // El repositorio es final
    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final ProductoImagenRepository productoImagenRepository;
    private final VarianteRepository varianteRepository;
    private final ColorRepository colorRepository;

    public ProductoService(ProductoRepository productoRepository, FirebaseStorageService firebaseStorageService, ProductoImagenRepository productoImagenRepository, VarianteRepository varianteRepository, ColorRepository colorRepository) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.productoImagenRepository = productoImagenRepository;
        this.varianteRepository = varianteRepository;
        this.colorRepository = colorRepository;
    }
    
    // Productos Activos
    @Transactional(readOnly = true)
    public List<Producto> getProductosA(boolean activo) {
        if (activo) { //Sólo activos...            
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }
    
    // Productos Destacados
    @Transactional(readOnly = true)
    public List<Producto> getProductosD(boolean destacado) {
        if (destacado) { //Sólo activos...            
            return productoRepository.findByDestacadoTrue();
        }
        return productoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }
    
    @Transactional
    public void actualizarVariante(Integer idVariante, String tamano, BigDecimal precioExtra) {
        Variante v = varianteRepository.findById(idVariante).orElseThrow();
        v.setTamano(tamano);
        v.setPrecioExtra(precioExtra);
        varianteRepository.save(v);
    }
    
    @Transactional
    public void agregarVariante(Integer productoId, Variante variante) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow();

        variante.setProducto(producto);

        varianteRepository.save(variante);
    }
    
    @Transactional
    public void eliminarVariante(Integer idVariante) {

        if (!varianteRepository.existsById(idVariante)) {
            throw new IllegalArgumentException("La variante no existe");
        }

        varianteRepository.deleteById(idVariante);
    }
    
    @Transactional
    public void actualizarColor(Integer idColor, String nombre, String codigoHex) {
        Color c = colorRepository.findById(idColor).orElseThrow();
        c.setNombre(nombre);
        c.setCodigoHex(codigoHex);
        colorRepository.save(c);
    }
    
    @Transactional
    public void agregarColor(Integer productoId, Color color) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow();

        color.setProducto(producto);

        colorRepository.save(color);
    }
    
    @Transactional
    public void eliminarColor(Integer idColor) {

        if (!colorRepository.existsById(idColor)) {
            throw new IllegalArgumentException("El color no existe");
        }

        colorRepository.deleteById(idColor);
    }
    
    @Transactional
    public void save(Producto producto, MultipartFile[] imagenFiles) {

        Producto target;

        if (producto.getIdProducto() != null) {
            // ✅ Edición: trabajar SOLO sobre el objeto existente en BD
            target = productoRepository.findById(producto.getIdProducto()).orElseThrow();
            target.setNombre(producto.getNombre());
            target.setDescripcion(producto.getDescripcion());
            target.setPrecioColones(producto.getPrecioColones());
            target.setExistencias(producto.getExistencias());
            target.setTipoLuz(producto.getTipoLuz());
            target.setMaterial(producto.getMaterial());
            target.setTipoConexion(producto.getTipoConexion());
            target.setDuracionBateria(producto.getDuracionBateria());
            target.setActivo(producto.getActivo());
            target.setDestacado(producto.getDestacado());
            target.setColeccion(producto.getColeccion());
        } else {
            target = producto;
        }

        target = productoRepository.save(target);

        if (imagenFiles != null) {
            for (MultipartFile file : imagenFiles) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String url = firebaseStorageService.uploadImage(
                                file, target.getIdProducto());
                        ProductoImagen img = new ProductoImagen();
                        img.setRutaImagen(url);
                        img.setProducto(target);
                        productoImagenRepository.save(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Transactional
    public void deleteImagen(Integer idImagen) {
        productoImagenRepository.findById(idImagen).ifPresent(img -> {
            firebaseStorageService.deleteImage(img.getRutaImagen()); // ✅ borra de Firebase
        });
        productoImagenRepository.deleteById(idImagen);
    }

    @Transactional
    public void delete(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
        }
        try {
            List<ProductoImagen> imagenes = productoImagenRepository.findByProductoIdProducto(idProducto);
            for (ProductoImagen img : imagenes) {
                firebaseStorageService.deleteImage(img.getRutaImagen());
            }
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el producto. Tiene datos asociados.", e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Producto> consultaDerivada(BigDecimal precioInf, BigDecimal precioSup) {
        return productoRepository.findByPrecioColonesBetweenOrderByPrecioColonesAsc(precioInf, precioSup);
    }
     
    @Transactional(readOnly = true)
    public List<Producto> buscarProductos(String buscar) {
        return productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(buscar);
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductosOrdenados(String orden) {
        return switch (orden) {
            case "precio_asc" ->
                productoRepository.findByActivoTrueOrderByPrecioColonesAsc();
            case "precio_desc" ->
                productoRepository.findByActivoTrueOrderByPrecioColonesDesc();
            case "nombre_asc" ->
                productoRepository.findByActivoTrueOrderByNombreAsc();
            case "nombre_desc" ->
                productoRepository.findByActivoTrueOrderByNombreDesc();
            case "nuevo" ->
                productoRepository.findByActivoTrueOrderByIdProductoDesc();
            case "viejo" ->
                productoRepository.findByActivoTrueOrderByIdProductoAsc();
            default ->
                productoRepository.findByActivoTrue();
        };
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductosActivosPorColeccion(Integer coleccionId) {
        return productoRepository.findByActivoTrueAndColeccionIdOrderByNombreAsc(coleccionId);
    }
}
