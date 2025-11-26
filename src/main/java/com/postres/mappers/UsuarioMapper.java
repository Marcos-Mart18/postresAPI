package com.postres.mappers;

import com.postres.dto.UsuarioDTO;
import com.postres.entity.Usuario;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PersonaMapper.class})
public interface UsuarioMapper extends BaseMappers<Usuario, UsuarioDTO> {
    @Override
    @Mapping(source = "repartidor.idRepartidor", target = "idRepartidor")
    UsuarioDTO toDTO(Usuario entity);

    @Override
    @Mapping(source = "idRepartidor", target = "repartidor.idRepartidor")
    Usuario toEntity(UsuarioDTO dto);
}
