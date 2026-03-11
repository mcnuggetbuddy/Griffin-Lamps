package com.GriffinLamps.pagina.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer id;

    @Column(length = 100)
    @Size(max = 100)
    private String nombre;

    @Column(length = 1000)
    @Size(max = 1000)
    private String descripcion;

    @Column(name = "precio_colones")
    private Double precioColones;

    @Column(name = "tipo_luz", length = 50)
    @Size(max = 50)
    private String tipoLuz;

    @Column(length = 100)
    @Size(max = 100)
    private String material;

    @Column(name = "tipo_conexion", length = 50)
    @Size(max = 50)
    private String tipoConexion;

    @Column(name = "duracion_bateria", length = 50)
    @Size(max = 50)
    private String duracionBateria;

    private Boolean activo;

    private Boolean destacado;

    @Column(name = "coleccion_id")
    private Integer coleccionId;
}
