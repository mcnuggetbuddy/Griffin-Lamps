package com.GriffinLamps.pagina.Domain;

public enum EstadoPedido {
    Pendiente("Pendiente"),
    Pagado("Pagado"),
    Cancelado("Cancelado");

    private final String valorBD;

    EstadoPedido(String valorBD) {
        this.valorBD = valorBD;
    }
    public String getValorBD() {
        return valorBD;
    }
}
