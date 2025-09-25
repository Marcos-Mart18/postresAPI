package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.UsuarioRolDTO;
import com.postres.entity.Rol;
import com.postres.entity.Usuario;
import com.postres.entity.UsuarioRol;
import com.postres.mappers.UsuarioRolMapper;
import com.postres.repository.RolRepository;
import com.postres.repository.UsuarioRepository;
import com.postres.repository.UsuarioRolRepository;
import com.postres.service.service.UsuarioRolService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioRolServiceImpl implements UsuarioRolService {
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolMapper usuarioRolMapper;

    public UsuarioRolServiceImpl(UsuarioRolRepository usuarioRolRepository,RolRepository rolRepository,UsuarioRepository usuarioRepository, UsuarioRolMapper usuarioRolMapper) {
        this.usuarioRolRepository = usuarioRolRepository;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolMapper = usuarioRolMapper;
    }

    @Override
    public UsuarioRolDTO create(UsuarioRolDTO usuarioRolDTO) throws ServiceException {
        try {
            // Validar que el ID de Usuario y el ID de Rol no sean nulos
            if (usuarioRolDTO.getIdUsuario() == null || usuarioRolDTO.getIdRol() == null) {
                throw new ServiceException("El Usuario o el Rol no pueden ser nulos");
            }

            // Buscar el Usuario y el Rol en la base de datos
            Usuario usuario = usuarioRepository.findById(usuarioRolDTO.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            Rol rol = rolRepository.findById(usuarioRolDTO.getIdRol())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

            // Convertir el DTO a la entidad
            UsuarioRol usuarioRol = usuarioRolMapper.toEntity(usuarioRolDTO);
            usuarioRol.setUsuario(usuario);  // Establecer el objeto Usuario completo
            usuarioRol.setRol(rol);          // Establecer el objeto Rol completo

            // Guardar la entidad en la base de datos
            UsuarioRol usuarioRolSaved = usuarioRolRepository.save(usuarioRol);

            // Convertir la entidad guardada de vuelta a DTO y devolver
            return usuarioRolMapper.toDTO(usuarioRolSaved);

        } catch (ResourceNotFoundException e) {
            throw e;  // Lanzar la excepción si no se encuentra el recurso
        } catch (Exception e) {
            // Lanzar un error más específico
            throw new ServiceException("Error al crear UsuarioRol", e);
        }
    }

    @Override
    public UsuarioRolDTO update(Long aLong, UsuarioRolDTO usuarioRolDTO) throws ServiceException {
        try {
            // Buscar la entidad UsuarioRol por ID
            UsuarioRol usuarioRol = usuarioRolRepository.findById(aLong)
                    .orElseThrow(() -> new ResourceNotFoundException("UsuarioRol no encontrado"));

            // Obtener el objeto Usuario completo usando el ID del usuario del DTO
            Usuario usuario = usuarioRepository.findById(usuarioRolDTO.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Obtener el objeto Rol completo usando el ID del rol del DTO
            Rol rol = rolRepository.findById(usuarioRolDTO.getIdRol())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

            // Establecer los objetos Usuario y Rol completos en UsuarioRol
            usuarioRol.setUsuario(usuario);
            usuarioRol.setRol(rol);

            // Guardar la entidad UsuarioRol actualizada
            UsuarioRol usuarioRolUpdated = usuarioRolRepository.save(usuarioRol);

            // Convertir la entidad actualizada a DTO y devolver
            return usuarioRolMapper.toDTO(usuarioRolUpdated);
        } catch (ResourceNotFoundException e) {
            throw e;  // Lanzar la excepción si no se encuentra el recurso
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar UsuarioRol", e);  // Manejar cualquier otra excepción
        }
    }

    @Override
    public UsuarioRolDTO findById(Long aLong) throws ServiceException {
        try {
            UsuarioRol usuarioRol = usuarioRolRepository.findById(aLong).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
            return usuarioRolMapper.toDTO(usuarioRol);
        } catch (ResourceNotFoundException e) {
            throw (e);
        } catch (Exception e) {
            throw new ServiceException("Error al leer la categoría con id " + aLong, e);
        }
    }

    @Override
    public void deleteById(Long aLong) throws ServiceException {
        try {
            if(!usuarioRepository.findById(aLong).isPresent()){
                throw new ResourceNotFoundException("Categoria no encontrada");
            }
            usuarioRolRepository.deleteById(aLong);
        }catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al eliminar la categoría con id " + aLong, e);
        }
    }

    @Override
    public List<UsuarioRolDTO> findAll() throws ServiceException {
        try {
            List<UsuarioRol> usuarioRoles = usuarioRolRepository.findAll();
            return usuarioRolMapper.toDTOs(usuarioRoles);
        }catch (Exception e) {
            throw new ServiceException("Error al listar las categorías",e);
        }
    }
}
