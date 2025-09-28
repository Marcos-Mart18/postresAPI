package com.postres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponseDto {
    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String profileFotoUrl;
    private String username;
    private List<String> roles;
}
