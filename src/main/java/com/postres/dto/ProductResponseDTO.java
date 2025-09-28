package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponseDTO {
    private Long idProducto;
    private String nombre;
    private Double precio;
    private String fotoUrl;
    private String descripcion;
    private CategoriaDTO categoria;
}
