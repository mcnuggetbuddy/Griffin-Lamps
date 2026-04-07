/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.GriffinLamps.pagina.Repository;

import com.GriffinLamps.pagina.Domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    public List<Producto> findByActivoTrue();
    
    public List<Producto> findByDestacadoTrue();

    //Consulta derivada que recupera los producto de un rango de precio y los ordena por precio ascendentemente
    public List<Producto> findByPrecioColonesBetweenOrderByPrecioColonesAsc(BigDecimal min, BigDecimal max);
    
    List<Producto> findByActivoTrueOrderByPrecioColonesAsc();
    List<Producto> findByActivoTrueOrderByPrecioColonesDesc();
    List<Producto> findByActivoTrueOrderByNombreAsc();
    List<Producto> findByActivoTrueOrderByNombreDesc();
    List<Producto> findByActivoTrueOrderByIdProductoAsc();
    List<Producto> findByActivoTrueOrderByIdProductoDesc();

    // Búsqueda por nombre (insensible a mayúsculas)
    List<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre);
    
    // Productos activos por colección para vista cliente
    List<Producto> findByActivoTrueAndColeccionIdOrderByNombreAsc(Integer coleccionId);
}
