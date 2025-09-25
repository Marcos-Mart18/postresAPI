package com.postres.mappers.base;

import java.util.List;

public interface BaseMappers <E,DTO>{
    DTO toDTO(E entity);
    E toEntity(DTO dto);
    List<DTO> toDTOs(List<E> list);
    List<E> toEntityList(List<DTO> list);
}
