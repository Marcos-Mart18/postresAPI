package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductoDTO {
    private Long idProducto;
    private String nombre;
    private String fotoUrl;
    private Double precio;
    private String descripcion;
    private Long idCategoria;
}
