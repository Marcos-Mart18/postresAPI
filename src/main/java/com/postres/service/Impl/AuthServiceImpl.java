package com.postres.service.Impl;

import com.postres.config.JwtTokenProvider;
import com.postres.dto.LoginDto;
import com.postres.dto.RegisterDto;
import com.postres.dto.RegisterRepartidorDTO;
import com.postres.dto.RepartidorDTO;
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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> login(LoginDto loginDto) {
        // 1. Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Buscar el usuario para incluir datos adicionales en los tokens
        Usuario usuario = usuarioRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();

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

        // 6. Retornar ambos tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    @Override
    public String register(RegisterDto registerDto) {

        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }

        if (usuarioRepository.existsByCorreo(registerDto.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Buscar el rol 'CLIENTE' en la base de datos
        Rol rol = rolRepository.findByNombre("CLIENTE")  // Aquí se busca el rol por su nombre
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear y guardar la entidad Persona
        Persona persona = new Persona();
        persona.setTelefono(registerDto.getTelefono());
        persona.setDireccion(registerDto.getDireccion());
        persona.setIsActive('A'); // Por ejemplo, asignamos como activo

        // Guardamos la persona en la base de datos
        personaRepository.save(persona);  // Guardar la entidad persona

        // Crear y guardar el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registerDto.getUsername());
        usuario.setCorreo(registerDto.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(registerDto.getPassword()));
        usuario.setNombres(registerDto.getNombres());
        usuario.setDni(registerDto.getDni());
        usuario.setApellidos(registerDto.getApellidos());
        usuario.setPersona(persona);  // Vincular la persona con el usuario

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
        if (usuarioRepository.existsByCorreo(registerDto.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Buscar el rol 'CLIENTE' en la base de datos
        Rol rol = rolRepository.findByNombre("ADMIN")  // Aquí se busca el rol por su nombre
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear y guardar la entidad Persona
        Persona persona = new Persona();
        persona.setTelefono(registerDto.getTelefono());
        persona.setDireccion(registerDto.getDireccion());
        persona.setIsActive('A'); // Por ejemplo, asignamos como activo

        // Guardamos la persona en la base de datos
        personaRepository.save(persona);  // Guardar la entidad persona

        // Crear y guardar el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registerDto.getUsername());
        usuario.setCorreo(registerDto.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(registerDto.getPassword()));
        usuario.setNombres(registerDto.getNombres());
        usuario.setDni(registerDto.getDni());
        usuario.setApellidos(registerDto.getApellidos());
        usuario.setPersona(persona);  // Vincular la persona con el usuario

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

        if (usuarioRepository.existsByCorreo(registerRepartidorDTO.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Buscar el rol 'CLIENTE' en la base de datos
        Rol rol = rolRepository.findByNombre("USER")  // Aquí se busca el rol por su nombre
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear y guardar la entidad Repartidor
        Repartidor repartidor = new Repartidor();
        repartidor.setCodigo(registerRepartidorDTO.getCodigo());
        repartidor.setIsActive('A');

        repartidorRepository.save(repartidor);

        // Crear y guardar el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registerRepartidorDTO.getUsername());
        usuario.setCorreo(registerRepartidorDTO.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(registerRepartidorDTO.getPassword()));
        usuario.setNombres(registerRepartidorDTO.getNombres());
        usuario.setApellidos(registerRepartidorDTO.getApellidos());
        usuario.setDni(registerRepartidorDTO.getDni());
        usuario.setRepartidor(repartidor);  // Vincular la persona con el usuario


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
