package com.postres.mappers;

import com.postres.dto.PedidoDTO;
import com.postres.entity.Pedido;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EstadoMapper.class, DetallePedidoMapper.class})
public interface PedidoMapper extends BaseMappers<Pedido, PedidoDTO> {
    @Override
    @Mapping(source = "detallePedidos", target = "detalles")
    PedidoDTO toDTO(Pedido entity);
}
