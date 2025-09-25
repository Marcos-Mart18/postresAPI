package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.CategoriaDTO;
import com.postres.entity.Categoria;
import com.postres.mappers.CategoriaMapper;
import com.postres.repository.CategoriaRepository;
import com.postres.service.service.CategoriaService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Override
    public CategoriaDTO create(CategoriaDTO categoriaDTO) throws ServiceException {
        try {
            Categoria categoria =categoriaMapper.toEntity(categoriaDTO);
            Categoria categoriaSaved =categoriaRepository.save(categoria);
            return categoriaMapper.toDTO(categoriaSaved);
        } catch (Exception e) {
            throw new ServiceException("Error al crear Categoría",e);
        }
    }

    @Override
    public CategoriaDTO update(Long aLong, CategoriaDTO categoriaDTO) throws ServiceException {
        try {
            Categoria categoria = categoriaRepository.findById(aLong).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
            categoria.setNombre(categoriaDTO.getNombre());
            Categoria categoriaUpdate = categoriaRepository.save(categoria);
            return categoriaMapper.toDTO(categoriaUpdate);
        } catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al actualizar Categoria",e);
        }
    }

    @Override
    public CategoriaDTO findById(Long aLong) throws ServiceException {
        try {
            Categoria categoria = categoriaRepository.findById(aLong).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
            return categoriaMapper.toDTO(categoria);
        } catch (ResourceNotFoundException e) {
            throw (e);
        } catch (Exception e) {
            throw new ServiceException("Error al leer la categoría con id " + aLong, e);
        }
    }

    @Override
    public void deleteById(Long aLong) throws ServiceException {
        try {
            if(!categoriaRepository.findById(aLong).isPresent()){
                throw new ResourceNotFoundException("Categoria no encontrada");
            }
            categoriaRepository.deleteById(aLong);
        }catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al eliminar la categoría con id " + aLong, e);
        }
    }

    @Override
    public List<CategoriaDTO> findAll() throws ServiceException {
        try {
            List<Categoria> categorias = categoriaRepository.findAll();
            return categoriaMapper.toDTOs(categorias);
        }catch (Exception e) {
            throw new ServiceException("Error al listar las categorías",e);
        }
    }
}
