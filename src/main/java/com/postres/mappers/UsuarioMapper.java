package com.postres.mappers;

import com.postres.dto.UsuarioDTO;
import com.postres.entity.Usuario;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends BaseMappers<Usuario, UsuarioDTO> {
}
