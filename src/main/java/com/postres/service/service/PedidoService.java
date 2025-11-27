package com.postres.service.service;

import com.postres.dto.PedidoDTO;
import com.postres.dto.PedidoResumenDTO;
import com.postres.dto.PedidoDetalleViewDTO;
import com.postres.entity.Pedido;
import com.postres.service.base.GenericService;

import java.util.List;
import com.postres.dto.DetallePedidoDTO;

public interface PedidoService extends GenericService<Pedido, PedidoDTO, Long> {
    PedidoDTO createByUser(PedidoDTO pedidoDTO, String username);
    
    PedidoDTO actualizarEstado(Long idPedido, Long idEstado, String usernameRepartidor);
    
    List<PedidoResumenDTO> obtenerResumenPorRepartidor(String usernameRepartidor);
    
    List<PedidoResumenDTO> obtenerResumenPorCliente(String usernameCliente);

    List<PedidoResumenDTO> listarResumenGeneral();

    PedidoDetalleViewDTO obtenerDetallePedido(Long idPedido);

    // Admin operations
    PedidoDTO aceptarPedido(Long idPedido);
    PedidoDTO marcarEnPreparacion(Long idPedido);
    PedidoDTO marcarListoParaEntrega(Long idPedido);
    PedidoDTO asignarRepartidor(Long idPedido, Long idRepartidor);
    PedidoDTO cancelarPedido(Long idPedido);

    // Repartidor operations
    PedidoDTO iniciarEntrega(Long idPedido, String usernameRepartidor);
    PedidoDTO marcarEntregado(Long idPedido, String usernameRepartidor);

    // Cliente
    PedidoDTO agregarDetalles(Long idPedido, List<DetallePedidoDTO> nuevos, String username);
}
