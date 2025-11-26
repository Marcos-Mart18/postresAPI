package com.postres.mappers;

import com.postres.dto.DetallePedidoDTO;
import com.postres.entity.DetallePedido;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DetallePedidoMapper extends BaseMappers<DetallePedido, DetallePedidoDTO> {
    @Override
    @Mapping(source = "pedido.idPedido", target = "idPedido")
    @Mapping(source = "producto.idProducto", target = "idProducto")
    DetallePedidoDTO toDTO(DetallePedido entity);
}
