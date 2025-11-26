package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String username;
    private String contrasena;
    private String profileFotoUrl;
    private PersonaDTO persona;
    private Long idRepartidor;
}
