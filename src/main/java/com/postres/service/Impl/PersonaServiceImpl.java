package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.PersonaDTO;
import com.postres.entity.Persona;
import com.postres.mappers.PersonaMapper;
import com.postres.repository.PersonaRepository;
import com.postres.service.service.PersonaService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaServiceImpl implements PersonaService {
    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;

    public PersonaServiceImpl(PersonaRepository personaRepository, PersonaMapper personaMapper) {
        this.personaRepository = personaRepository;
        this.personaMapper = personaMapper;
    }

    @Override
    public PersonaDTO create(PersonaDTO personaDTO) throws ServiceException {
        try {
            Persona persona = new Persona();
            persona.setNombres(personaDTO.getNombres());
            persona.setApellidos(personaDTO.getApellidos());
            persona.setDni(personaDTO.getDni());
            persona.setCorreo(personaDTO.getCorreo());
            persona.setTelefono(personaDTO.getTelefono());
            persona.setDireccion(personaDTO.getDireccion());
            persona.setIsActive('A');
            Persona saved = personaRepository.save(persona);
            return personaMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ServiceException("Error al crear la Persona", e);
        }
    }

    @Override
    public PersonaDTO update(Long id, PersonaDTO personaDTO) throws ServiceException {
        try {
            Persona persona = personaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada"));
            persona.setNombres(personaDTO.getNombres());
            persona.setApellidos(personaDTO.getApellidos());
            persona.setDni(personaDTO.getDni());
            persona.setCorreo(personaDTO.getCorreo());
            persona.setTelefono(personaDTO.getTelefono());
            persona.setDireccion(personaDTO.getDireccion());
            Persona updated = personaRepository.save(persona);
            return personaMapper.toDTO(updated);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar la Persona", e);
        }
    }

    @Override
    public PersonaDTO findById(Long id) throws ServiceException {
        try {
            Persona persona = personaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada"));
            return personaMapper.toDTO(persona);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al leer la Persona con id " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            if (!personaRepository.findById(id).isPresent()) {
                throw new ResourceNotFoundException("Persona no encontrada");
            }
            personaRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar la Persona con id " + id, e);
        }
    }

    @Override
    public List<PersonaDTO> findAll() throws ServiceException {
        try {
            List<Persona> personas = personaRepository.findAll();
            return personaMapper.toDTOs(personas);
        } catch (Exception e) {
            throw new ServiceException("Error al listar las Personas", e);
        }
    }
}
