package com.postres.mappers;

import com.postres.dto.RefreshTokenDTO;
import com.postres.entity.RefreshToken;
import com.postres.mappers.base.BaseMappers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper extends BaseMappers<RefreshToken, RefreshTokenDTO> {
}
