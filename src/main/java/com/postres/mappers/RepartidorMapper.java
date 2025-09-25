package com.postres.mappers;

import com.postres.dto.RepartidorDTO;
import com.postres.entity.Repartidor;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RepartidorMapper extends BaseMappers<Repartidor, RepartidorDTO> {
}
