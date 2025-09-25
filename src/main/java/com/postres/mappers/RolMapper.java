package com.postres.mappers;

import com.postres.dto.RolDTO;
import com.postres.entity.Rol;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RolMapper extends BaseMappers<Rol, RolDTO> {
}
