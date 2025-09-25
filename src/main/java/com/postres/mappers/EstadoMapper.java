package com.postres.mappers;

import com.postres.dto.EstadoDTO;
import com.postres.entity.Estado;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstadoMapper extends BaseMappers<Estado, EstadoDTO> {
}
