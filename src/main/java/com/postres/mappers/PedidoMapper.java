package com.postres.mappers;

import com.postres.dto.PedidoDTO;
import com.postres.entity.Pedido;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PedidoMapper extends BaseMappers<Pedido, PedidoDTO> {
}
