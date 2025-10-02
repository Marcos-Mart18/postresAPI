package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductoRequestDTO {
    private String nombre;
    private Double precio;
    private String descripcion;
    private Long idCategoria;
}
