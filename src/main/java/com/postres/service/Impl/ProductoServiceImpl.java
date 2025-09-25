package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.ProductoDTO;
import com.postres.entity.Categoria;
import com.postres.entity.Producto;
import com.postres.mappers.ProductoMapper;
import com.postres.repository.CategoriaRepository;
import com.postres.repository.ProductoRepository;
import com.postres.service.service.ProductoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(ProductoRepository productoRepository,CategoriaRepository categoriaRepository, ProductoMapper productoMapper){
        this.productoRepository=productoRepository;
        this.categoriaRepository=categoriaRepository;
        this.productoMapper=productoMapper;
    }

    @Override
    public ProductoDTO create(ProductoDTO productoDTO) throws ServiceException {
        try {
            // Buscar la categoría usando el idCategoria
            Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            Producto producto = new Producto();
            producto.setNombre(productoDTO.getNombre());
            producto.setPrecio(productoDTO.getPrecio());
            producto.setDescripcion(productoDTO.getDescripcion());
            producto.setCategoria(categoria);  // Asignar la categoría encontrada

            // Guardar el producto en la base de datos
            Producto productoSaved = productoRepository.save(producto);

            // Convertir la entidad Producto guardada a ProductoDTO
            return productoMapper.toDTO(productoSaved);
        } catch (Exception e) {
            throw new ServiceException("Error al crear el Producto", e);
        }
    }

    @Override
    public ProductoDTO update(Long aLong, ProductoDTO productoDTO) throws ServiceException {
        try {
            // Buscar el producto por su ID
            Producto producto = productoRepository.findById(aLong)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            // Actualizar los campos del producto con los valores del DTO
            producto.setNombre(productoDTO.getNombre());
            producto.setPrecio(productoDTO.getPrecio());
            producto.setDescripcion(productoDTO.getDescripcion());

            // Buscar la categoría asociada usando el ID de categoría en el DTO
            Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

            // Asignar la categoría al producto actualizado
            producto.setCategoria(categoria);

            // Guardar el producto actualizado en la base de datos
            Producto productoUpdated = productoRepository.save(producto);

            // Convertir la entidad Producto actualizada a ProductoDTO
            return productoMapper.toDTO(productoUpdated);
        } catch (ResourceNotFoundException e) {
            throw e;  // Si no se encuentra el producto o la categoría, lanzar la excepción
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar el Producto", e);  // Manejo de excepciones generales
        }
    }

    @Override
    public ProductoDTO findById(Long aLong) throws ServiceException {
        try {
            Producto producto = productoRepository.findById(aLong).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
            return productoMapper.toDTO(producto);
        } catch (ResourceNotFoundException e) {
            throw (e);
        } catch (Exception e) {
            throw new ServiceException("Error al leer la categoría con id " + aLong, e);
        }
    }

    @Override
    public void deleteById(Long aLong) throws ServiceException {
        try {
            if(!productoRepository.findById(aLong).isPresent()){
                throw new ResourceNotFoundException("Categoria no encontrada");
            }
            productoRepository.deleteById(aLong);
        }catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al eliminar la categoría con id " + aLong, e);
        }
    }

    @Override
    public List<ProductoDTO> findAll() throws ServiceException {
        try {
            List<Producto> productos = productoRepository.findAll();
            return productoMapper.toDTOs(productos);
        }catch (Exception e) {
            throw new ServiceException("Error al listar las categorías",e);
        }
    }
}
