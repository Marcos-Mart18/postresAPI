package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PedidoDTO {
    private Long idPedido;
    private String numOrden;
    @NotNull
    @Future
    private LocalDate fechaEntrega;
    @NotBlank
    private String horaEntrega;
    @DecimalMin("0.0")
    private Double total;
    @NotBlank
    private String direccion;
    private EstadoDTO estado;
    private List<DetallePedidoDTO> detalles;

}
