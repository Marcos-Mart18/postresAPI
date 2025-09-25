package com.postres.mappers;

import com.postres.dto.PersonaDTO;
import com.postres.entity.Persona;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonaMapper extends BaseMappers<Persona, PersonaDTO> {
}
