package com.GriffinLamps.pagina.Domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "color")
public class Color implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idColor;

    private String nombre;

    @Column(name = "codigo_hex")
    private String codigoHex;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

}
