package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.CategoriaDTO;
import com.postres.dto.ProductResponseDTO;
import com.postres.dto.ProductoDTO;
import com.postres.dto.ProductoRequestDTO;
import com.postres.entity.Categoria;
import com.postres.entity.Estado;
import com.postres.entity.Producto;
import com.postres.mappers.ProductoMapper;
import com.postres.repository.CategoriaRepository;
import com.postres.repository.ProductoRepository;
import com.postres.service.service.ProductoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final CloudinaryService cloudinaryService;

    public ProductoServiceImpl(ProductoRepository productoRepository,CategoriaRepository categoriaRepository, ProductoMapper productoMapper,CloudinaryService cloudinaryService){
        this.productoRepository=productoRepository;
        this.categoriaRepository=categoriaRepository;
        this.productoMapper=productoMapper;
        this.cloudinaryService = cloudinaryService;
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

    @Override
    public List<ProductResponseDTO> findAllProductos() {
        try {
            List<Producto> productos = productoRepository.findAll();

            return productos.stream()
                    .map(producto -> new ProductResponseDTO(
                            producto.getIdProducto(),
                            producto.getNombre(),
                            producto.getPrecio(),
                            producto.getFotoUrl(),
                            producto.getDescripcion(),
                            producto.getCategoria() != null
                                    ? new CategoriaDTO(
                                    producto.getCategoria().getIdCategoria(),
                                    producto.getCategoria().getNombre()
                            )
                                    : null
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new ServiceException("Error al listar los productos", e);
        }
    }

    @Override
    public ProductResponseDTO findProductById(Long id) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            CategoriaDTO categoria = producto.getCategoria() != null
                    ? new CategoriaDTO(
                            producto.getCategoria().getIdCategoria(),
                            producto.getCategoria().getNombre()
                    )
                    : null;

            return new ProductResponseDTO(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getFotoUrl(),
                    producto.getDescripcion(),
                    categoria
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al leer el producto con id " + id, e);
        }
    }

    @Override
    public ProductoDTO createWithImage(ProductoRequestDTO productoRequest, MultipartFile file) {
        try {
            Producto producto = new Producto();
            producto.setNombre(productoRequest.getNombre());
            producto.setPrecio(productoRequest.getPrecio());
            producto.setDescripcion(productoRequest.getDescripcion());

            Categoria categoria = categoriaRepository.findById(productoRequest.getIdCategoria())
                    .orElseThrow(() -> new ServiceException("Categoría no encontrada"));
            producto.setCategoria(categoria);

            producto.setIsActive('A');

            producto = productoRepository.save(producto);

            String imageUrl = cloudinaryService.uploadImage(file, producto.getIdProducto());

            producto.setFotoUrl(imageUrl);

            producto = productoRepository.save(producto);

            ProductoDTO productoDTO = new ProductoDTO(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getFotoUrl(),
                    producto.getDescripcion(),
                    producto.getCategoria().getIdCategoria() // Se asume que necesitas devolver el id de la categoría
            );

            // Retornar el ProductoDTO con la imagen subida
            return productoDTO;

        } catch (Exception e) {
            throw new ServiceException("Error al crear el Producto con la imagen", e);
        }
    }




}
