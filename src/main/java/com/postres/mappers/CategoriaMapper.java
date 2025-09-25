package com.postres.mappers;

import com.postres.dto.CategoriaDTO;
import com.postres.entity.Categoria;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper extends BaseMappers<Categoria, CategoriaDTO> {
}
