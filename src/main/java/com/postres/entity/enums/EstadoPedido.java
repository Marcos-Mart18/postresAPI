package com.postres.entity.enums;

public enum EstadoPedido {
    PENDIENTE(1L, "Pendiente"),
    EN_PREPARACION(2L, "En preparación"),
    EN_CAMINO(3L, "En camino"),
    ENTREGADO(4L, "Entregado"),
    CANCELADO(5L, "Cancelado");

    private final Long id;
    private final String descripcion;

    EstadoPedido(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoPedido getById(Long id) {
        for (EstadoPedido estado : values()) {
            if (estado.id.equals(id)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado no válido: " + id);
    }
}
