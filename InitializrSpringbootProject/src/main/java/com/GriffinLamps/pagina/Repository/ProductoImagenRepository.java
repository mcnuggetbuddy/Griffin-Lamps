/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.GriffinLamps.pagina.Repository;

import com.GriffinLamps.pagina.Domain.ProductoImagen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Integer>{
    List<ProductoImagen> findByProductoIdProducto(Integer idProducto);
}
