package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.RepartidorDTO;
import com.postres.entity.Repartidor;
import com.postres.mappers.RepartidorMapper;
import com.postres.repository.RepartidorRepository;
import com.postres.service.service.RepartidorService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepartidorServiceImpl implements RepartidorService {
    private final RepartidorRepository repartidorRepository;
    private final RepartidorMapper repartidorMapper;

    public RepartidorServiceImpl(RepartidorRepository repartidorRepository, RepartidorMapper repartidorMapper) {
        this.repartidorRepository = repartidorRepository;
        this.repartidorMapper = repartidorMapper;
    }

    @Override
    public RepartidorDTO create(RepartidorDTO repartidorDTO) throws ServiceException {
        try {
            Repartidor repartidor = new Repartidor();
            repartidor.setCodigo(repartidorDTO.getCodigo());
            repartidor.setIsActive('A');
            Repartidor saved = repartidorRepository.save(repartidor);
            return repartidorMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ServiceException("Error al crear el Repartidor", e);
        }
    }

    @Override
    public RepartidorDTO update(Long id, RepartidorDTO repartidorDTO) throws ServiceException {
        try {
            Repartidor repartidor = repartidorRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado"));
            repartidor.setCodigo(repartidorDTO.getCodigo());
            Repartidor updated = repartidorRepository.save(repartidor);
            return repartidorMapper.toDTO(updated);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar el Repartidor", e);
        }
    }

    @Override
    public RepartidorDTO findById(Long id) throws ServiceException {
        try {
            Repartidor repartidor = repartidorRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado"));
            return repartidorMapper.toDTO(repartidor);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al leer el Repartidor con id " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            if (!repartidorRepository.findById(id).isPresent()) {
                throw new ResourceNotFoundException("Repartidor no encontrado");
            }
            repartidorRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar el Repartidor con id " + id, e);
        }
    }

    @Override
    public List<RepartidorDTO> findAll() throws ServiceException {
        try {
            List<Repartidor> repartidores = repartidorRepository.findAll();
            return repartidorMapper.toDTOs(repartidores);
        } catch (Exception e) {
            throw new ServiceException("Error al listar los Repartidores", e);
        }
    }
}
