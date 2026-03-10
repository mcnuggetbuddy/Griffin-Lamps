package com.GriffinLamps.pagina.Domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía.")
    @Size(max = 1000)
    private String descripcion;

    @NotNull(message = "El precio no puede estar vacío.")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "precio_colones")
    private BigDecimal precioColones;
    
    @NotNull(message = "El campo de existencias no puede estar vacío.")
    @Min(value = 0, message = "Las existencias deben ser un número mayor o igual a 0.")
    private Integer existencias;

    @NotBlank(message = "El tipo de luz no puede estar vacío.")
    @Size(max = 50)
    @Column(name = "tipo_luz")
    private String tipoLuz;

    @NotBlank(message = "El tipo de material no puede estar vacío.")
    @Size(max = 100)
    private String material;

    @NotBlank(message = "El tipo de conexión no puede estar vacío.")
    @Size(max = 50)
    @Column(name = "tipo_conexion")
    private String tipoConexion;

    @Size(max = 50, message = "La duración de la batería no puede estar vacía.")
    @Column(name = "duracion_bateria")
    private String duracionBateria;

    @NotNull
    private Boolean activo;

    @NotNull
    private Boolean destacado;
    
    // RELACIONES
    // relación con colección
    @NotNull
    @ManyToOne
    @JoinColumn(name = "coleccion_id")
    private Coleccion coleccion;
    
    // relación con las imágenes
    @OneToMany(mappedBy="producto", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ProductoImagen> imagenes;

    // variantes del producto
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variante> variantes;

    // colores disponibles
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Color> colores;
    
    // HELPERS
    public void addImagen(ProductoImagen img) {
        imagenes.add(img);
        img.setProducto(this);
    }

    public void addVariante(Variante v) {
        variantes.add(v);
        v.setProducto(this);
    }

    public void addColor(Color c) {
        colores.add(c);
        c.setProducto(this);
    }
}
