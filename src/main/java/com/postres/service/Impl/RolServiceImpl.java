package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.RolDTO;
import com.postres.entity.Rol;
import com.postres.mappers.RolMapper;
import com.postres.repository.RolRepository;
import com.postres.service.service.RolService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RolServiceImpl implements RolService {
    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    public RolServiceImpl(RolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    public RolDTO create(RolDTO rolDTO) throws ServiceException {
        try {
            Rol rol =rolMapper.toEntity(rolDTO);
            Rol rolSaved =rolRepository.save(rol);
            return rolMapper.toDTO(rolSaved);
        } catch (Exception e) {
            throw new ServiceException("Error al crear rol",e);
        }
    }

    @Override
    public RolDTO update(Long aLong, RolDTO rolDTO) throws ServiceException {
        try {
            Rol rol = rolRepository.findById(aLong).orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
            rol.setNombre(rolDTO.getNombre());
            Rol rolUpdate = rolRepository.save(rol);
            return rolMapper.toDTO(rolUpdate);
        } catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al actualizar Rol",e);
        }
    }

    @Override
    public RolDTO findById(Long aLong) throws ServiceException {
        try {
            Rol rol = rolRepository.findById(aLong).orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
            return rolMapper.toDTO(rol);
        } catch (ResourceNotFoundException e) {
            throw (e);
        } catch (Exception e) {
            throw new ServiceException("Error al leer la rol con id " + aLong, e);
        }
    }

    @Override
    public void deleteById(Long aLong) throws ServiceException {
        try {
            if(!rolRepository.findById(aLong).isPresent()){
                throw new ResourceNotFoundException("Rol no encontrado");
            }
            rolRepository.deleteById(aLong);
        }catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al eliminar la rol con id " + aLong, e);
        }
    }

    @Override
    public List<RolDTO> findAll() throws ServiceException {
        try {
            List<Rol> roles = rolRepository.findAll();
            return rolMapper.toDTOs(roles);
        }catch (Exception e) {
            throw new ServiceException("Error al listar los roles",e);
        }
    }
}
