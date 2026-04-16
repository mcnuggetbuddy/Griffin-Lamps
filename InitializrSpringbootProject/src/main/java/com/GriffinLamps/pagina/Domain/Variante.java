package com.GriffinLamps.pagina.Domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "variante")
public class Variante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idVariante;

    @Column(name = "talla")
    private String tamano;

    @Column(name = "precio_extra")
    private BigDecimal precioExtra;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

}
