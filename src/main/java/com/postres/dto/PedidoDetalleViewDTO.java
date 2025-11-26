package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalleViewDTO {
    private Long idPedido;
    private String numOrden;
    private LocalDate fechaPedido;
    private LocalDate fechaEntrega;
    private String horaEntrega;
    private Double total;
    private String direccion;
    private EstadoDTO estado;
    private String repartidorNombre; // puede ser null
    private List<DetallePedidoItemDTO> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetallePedidoItemDTO {
        private Long cantidad;
        private ProductResponseDTO producto;
    }
}
