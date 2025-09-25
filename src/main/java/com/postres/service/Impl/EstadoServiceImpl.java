package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.EstadoDTO;
import com.postres.entity.Estado;
import com.postres.mappers.EstadoMapper;
import com.postres.repository.EstadoRepository;
import com.postres.service.service.EstadoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoServiceImpl implements EstadoService {
    private final EstadoRepository estadoRepository;
    private final EstadoMapper estadoMapper;

    public EstadoServiceImpl(EstadoRepository estadoRepository,EstadoMapper estadoMapper) {
        this.estadoRepository = estadoRepository;
        this.estadoMapper = estadoMapper;
    }

    @Override
    public EstadoDTO create(EstadoDTO estadoDTO) throws ServiceException {
        try {
            Estado estado =estadoMapper.toEntity(estadoDTO);
            Estado estadoSaved =estadoRepository.save(estado);
            return estadoMapper.toDTO(estadoSaved);
        } catch (Exception e) {
            throw new ServiceException("Error al crear Categoría",e);
        }
    }

    @Override
    public EstadoDTO update(Long aLong, EstadoDTO estadoDTO) throws ServiceException {
        try {
            Estado estado = estadoRepository.findById(aLong).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
            estado.setNombre(estadoDTO.getNombre());
            Estado estadoUpdate = estadoRepository.save(estado);
            return estadoMapper.toDTO(estadoUpdate);
        } catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al actualizar Categoria",e);
        }
    }

    @Override
    public EstadoDTO findById(Long aLong) throws ServiceException {
        try {
            Estado estado = estadoRepository.findById(aLong).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
            return estadoMapper.toDTO(estado);
        } catch (ResourceNotFoundException e) {
            throw (e);
        } catch (Exception e) {
            throw new ServiceException("Error al leer la categoría con id " + aLong, e);
        }
    }

    @Override
    public void deleteById(Long aLong) throws ServiceException {
        try {
            if(!estadoRepository.findById(aLong).isPresent()){
                throw new ResourceNotFoundException("Categoria no encontrada");
            }
            estadoRepository.deleteById(aLong);
        }catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al eliminar la categoría con id " + aLong, e);
        }
    }

    @Override
    public List<EstadoDTO> findAll() throws ServiceException {
        try {
            List<Estado> estados = estadoRepository.findAll();
            return estadoMapper.toDTOs(estados);
        }catch (Exception e) {
            throw new ServiceException("Error al listar las categorías",e);
        }
    }
}
