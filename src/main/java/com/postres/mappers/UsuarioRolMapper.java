package com.postres.mappers;

import com.postres.dto.UsuarioRolDTO;
import com.postres.entity.UsuarioRol;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioRolMapper extends BaseMappers<UsuarioRol, UsuarioRolDTO> {
}
