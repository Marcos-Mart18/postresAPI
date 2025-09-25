package com.postres.dto;

import lombok.Data;

@Data
public class RegisterRepartidorDTO {
    private String username;
    private String correo;
    private String password;
    private String nombres;
    private String apellidos;
    private String dni;
    private String codigo;
}
