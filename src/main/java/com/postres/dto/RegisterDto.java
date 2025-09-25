package com.postres.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String correo;
    private String password;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String dni;
    private String direccion;
}
