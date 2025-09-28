package com.postres.service.service;

import com.postres.dto.LoginDto;
import com.postres.dto.RegisterDto;
import com.postres.dto.RegisterRepartidorDTO;
import com.postres.entity.Usuario;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginDto loginDto); // Devuelve Access Token y Refresh Token
    String register(RegisterDto registerDto); // Registro de usuario
    String registerAdmin(RegisterDto registerDto); // Registro de admin
    String registerRepartidor(RegisterRepartidorDTO registerRepartidorDTO);
    Usuario findUserByUsername(String username); // Buscar usuario por nombre de usuario
    String refreshAccessToken(String refreshToken); // Generar nuevo Access Token usando Refresh Token
    void logout(String refreshToken); // Cerrar sesión e invalidar Refresh Token
}
