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
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String fotoUrl;
    private String correo;
}
