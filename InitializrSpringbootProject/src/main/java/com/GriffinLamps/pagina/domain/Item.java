package com.GriffinLamps.pagina.Domain;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private Producto producto;
    private int cantidad;
    private BigDecimal precioHistorico;
    private Color color;
    private Variante variante;
    private String rutaImagen;

    public BigDecimal getSubTotal() {
        return precioHistorico.multiply(new BigDecimal(cantidad));
    }
}