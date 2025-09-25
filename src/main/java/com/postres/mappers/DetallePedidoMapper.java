package com.postres.mappers;

import com.postres.dto.DetallePedidoDTO;
import com.postres.entity.DetallePedido;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DetallePedidoMapper extends BaseMappers<DetallePedido, DetallePedidoDTO> {
}
