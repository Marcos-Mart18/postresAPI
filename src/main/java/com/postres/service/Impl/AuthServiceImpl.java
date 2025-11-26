package com.postres.service.Impl;

import com.postres.config.JwtTokenProvider;
import com.postres.dto.*;
import com.postres.entity.*;
import com.postres.repository.PersonaRepository;
import com.postres.repository.RepartidorRepository;
import com.postres.repository.RolRepository;
import com.postres.repository.UsuarioRepository;
import com.postres.service.service.AuthService;
import com.postres.service.service.RefreshTokenService;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService{
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private RepartidorRepository repartidorRepository;


    @Override
    public Map<String, Object> login(LoginDto loginDto) {
        // 1. Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Buscar el usuario para incluir datos adicionales en los tokens
        Usuario usuario = usuarioRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String nombreCompleto = (usuario.getPersona() != null)
                ? (usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidos())
                : usuario.getUsername();


        // 3. Generar Access Token
        String accessToken = jwtTokenProvider.generateToken(authentication, nombreCompleto);

        // 4. Generar Refresh Token
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario.getUsername());

        // 5. Guardar Refresh Token en la base de datos
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setRefreshToken(refreshToken);
        tokenEntity.setUsuario(usuario);
        tokenEntity.setExpiryDate(new Date(System.currentTimeMillis() + 604800000));
        refreshTokenService.save(tokenEntity);

        List<String> roles = usuario.getUsuarioRoles().stream()
                .map(usuarioRol -> usuarioRol.getRol().getNombre())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("username", usuario.getUsername());
        response.put("roles", roles);
        response.put("profileFotoUrl", usuario.getProfileFotoUrl());
        response.put("idUsuario", usuario.getIdUsuario());

        return response;
    }

    @Override
    public String register(RegisterDto registerDto) {

        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }
        // Buscar el rol 'CLIENTE' en la base de datos
        Rol rol = rolRepository.findByNombre("CLIENTE")  // Aquí se busca el rol por su nombre
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear y guardar el usuario (mínimo: username y password, foto por defecto)
        Usuario usuario = new Usuario();
        usuario.setUsername(registerDto.getUsername());
        usuario.setContrasena(passwordEncoder.encode(registerDto.getPassword()));
        usuario.setProfileFotoUrl("https://res.cloudinary.com/demo/image/upload/v1/defaults/user.png");

        // Crear la relación entre el usuario y el rol
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol); // Asignar el rol encontrado de la base de datos

        usuario.setUsuarioRoles(Collections.singletonList(usuarioRol));

        // Guardar el usuario en la base de datos
        usuarioRepository.save(usuario);

        return "Usuario registrado con éxito con el rol: " + rol.getNombre();
    }

    @Override
    public String registerAdmin(RegisterDto registerDto) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Buscar el rol 'CLIENTE' en la base de datos
        Rol rol = rolRepository.findByNombre("ADMIN")  // Aquí se busca el rol por su nombre
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear y guardar el usuario (mínimo: username y password)
        Usuario usuario = new Usuario();
        usuario.setUsername(registerDto.getUsername());
        usuario.setContrasena(passwordEncoder.encode(registerDto.getPassword()));

        // Crear la relación entre el usuario y el rol
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol); // Asignar el rol encontrado de la base de datos

        usuario.setUsuarioRoles(Collections.singletonList(usuarioRol));

        // Guardar el usuario en la base de datos
        usuarioRepository.save(usuario);

        return "Usuario registrado con éxito con el rol: " + rol.getNombre();
    }

    @Override
    public String registerRepartidor(RegisterRepartidorDTO registerRepartidorDTO) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByUsername(registerRepartidorDTO.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Buscar el rol 'CLIENTE' en la base de datos
        Rol rol = rolRepository.findByNombre("REPARTIDOR")  // Aquí se busca el rol por su nombre
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear y guardar la entidad Repartidor
        Repartidor repartidor = new Repartidor();
        repartidor.setCodigo(registerRepartidorDTO.getCodigo());
        repartidor.setIsActive('A');

        repartidorRepository.save(repartidor);

        // Crear y guardar el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registerRepartidorDTO.getUsername());
        usuario.setContrasena(passwordEncoder.encode(registerRepartidorDTO.getPassword()));
        usuario.setRepartidor(repartidor);  // Vincular la persona con el usuario
        usuario.setProfileFotoUrl("https://res.cloudinary.com/demo/image/upload/v1/defaults/user.png");


        // Crear la relación entre el usuario y el rol
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol); // Asignar el rol encontrado de la base de datos

        usuario.setUsuarioRoles(Collections.singletonList(usuarioRol));

        // Guardar el usuario en la base de datos
        usuarioRepository.save(usuario);

        return "Usuario registrado con éxito con el rol: " + rol.getNombre();
    }

    @Override
    public Usuario findUserByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        return jwtTokenProvider.refreshAccessToken(refreshToken);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
    }
}
