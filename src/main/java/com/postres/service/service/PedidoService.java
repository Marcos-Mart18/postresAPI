package com.postres.service.service;

import com.postres.dto.PedidoDTO;
import com.postres.entity.Pedido;
import com.postres.service.base.GenericService;
import org.springframework.security.oauth2.jwt.Jwt;

public interface PedidoService extends GenericService<Pedido, PedidoDTO,Long> {
    PedidoDTO createByUser(PedidoDTO pedidoDTO, String username);
}
