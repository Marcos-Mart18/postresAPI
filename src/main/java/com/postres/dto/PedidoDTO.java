package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PedidoDTO {
    private LocalDate fechaEntrega;
    private String horaEntrega;
    private Double total;
    private String direccion;
    private Long idEstado;

}
