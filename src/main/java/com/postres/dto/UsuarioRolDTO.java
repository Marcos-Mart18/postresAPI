package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioRolDTO {
    private Long idUsuarioRol;
    private Long idUsuario;
    private Long idRol;
}
