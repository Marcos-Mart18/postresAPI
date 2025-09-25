package com.postres.mappers;

import com.postres.dto.ProductoDTO;
import com.postres.entity.Producto;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductoMapper extends BaseMappers<Producto, ProductoDTO> {
}
