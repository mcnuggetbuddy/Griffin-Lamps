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
    public void agregarVariante(Integer productoId, Variante variante) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow();

        variante.setProducto(producto);

        varianteRepository.save(variante);
    }
    
    @Transactional
    public void agregarColor(Integer productoId, Color color) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow();

        color.setProducto(producto);

        colorRepository.save(color);
    }
    
    @Transactional
    public void save(Producto producto, MultipartFile[] imagenFiles) {

        producto = productoRepository.save(producto);

        if (imagenFiles != null) {
            for (MultipartFile file : imagenFiles) {

                if (!file.isEmpty()) {

                    try {
                        String url = firebaseStorageService.uploadImage(
                                file, "producto", producto.getIdProducto());

                        ProductoImagen img = new ProductoImagen();
                        img.setRutaImagen(url);
                        img.setProducto(producto);

                        productoImagenRepository.save(img);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Transactional
    public void delete(Integer idProducto) {
        // Verifica si la categoría existe antes de intentar eliminarlo
        if (!productoRepository.existsById(idProducto)) {
            // Lanza una excepción para indicar que el usuario no fue encontrado
            throw new IllegalArgumentException("La categoría con ID " + idProducto + " no existe.");
        }
        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            // Lanza una nueva excepción para encapsular el problema de integridad de datos
            throw new IllegalStateException("No se puede eliminar la producto. Tiene datos asociados.", e);
        }
    }
    
    @Transactional
    public void deleteImagen(Integer idImagen){
        productoImagenRepository.deleteById(idImagen);
    }
                
        @Transactional(readOnly = true)
        public List<Producto> consultaDerivada(BigDecimal precioInf, BigDecimal precioSup){
            return productoRepository.findByPrecioColonesBetweenOrderByPrecioColonesAsc(precioInf, precioSup);
        }
     
}
