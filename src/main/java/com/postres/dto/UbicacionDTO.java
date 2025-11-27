package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UbicacionDTO {
    private Long pedidoId;
    private Long repartidorId;
    private Double latitud;
    private Double longitud;
    private String timestamp;
}
