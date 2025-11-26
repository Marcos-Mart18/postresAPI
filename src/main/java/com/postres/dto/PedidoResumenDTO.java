package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResumenDTO {
    private Long idPedido;
    private LocalDate fechaPedido;
    private LocalDate fechaEntrega;
    private Double total;
    private EstadoDTO estado;
    private String repartidorNombre; // puede ser null si no hay asignación
}
