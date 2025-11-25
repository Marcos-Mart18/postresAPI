package com.postres.service.service;

import com.postres.dto.PedidoDTO;
import com.postres.entity.Pedido;
import com.postres.service.base.GenericService;

import java.util.List;

public interface PedidoService extends GenericService<Pedido, PedidoDTO, Long> {
    PedidoDTO createByUser(PedidoDTO pedidoDTO, String username);
    
    PedidoDTO actualizarEstado(Long idPedido, Long idEstado, String usernameRepartidor);
    
    List<PedidoDTO> obtenerPedidosPorRepartidor(String usernameRepartidor);
    
    List<PedidoDTO> obtenerPedidosPorCliente(String usernameCliente);
}
