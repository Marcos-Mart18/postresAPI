package com.postres.controller.general;

import com.postres.dto.AsignarPerfilUsuarioDTO;
import com.postres.dto.UsuarioDTO;
import com.postres.entity.Persona;
import com.postres.entity.Repartidor;
import com.postres.entity.Usuario;
import com.postres.mappers.UsuarioMapper;
import com.postres.repository.PersonaRepository;
import com.postres.repository.RepartidorRepository;
import com.postres.repository.UsuarioRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RepartidorRepository repartidorRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             PersonaRepository personaRepository,
                             RepartidorRepository repartidorRepository,
                             UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.repartidorRepository = repartidorRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/perfil")
    public ResponseEntity<UsuarioDTO> asignarPerfil(@PathVariable Long id,
                                                    @RequestBody AsignarPerfilUsuarioDTO body) throws ServiceException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (body.getIdPersona() != null) {
            Persona persona = personaRepository.findById(body.getIdPersona())
                    .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
            usuario.setPersona(persona);
        } else {
            usuario.setPersona(null);
        }

        if (body.getIdRepartidor() != null) {
            Repartidor repartidor = repartidorRepository.findById(body.getIdRepartidor())
                    .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
            usuario.setRepartidor(repartidor);
        } else {
            usuario.setRepartidor(null);
        }

        Usuario saved = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuarioMapper.toDTO(saved));
    }
}
