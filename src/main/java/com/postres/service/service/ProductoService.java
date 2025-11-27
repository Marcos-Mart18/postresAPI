package com.postres.service.service;

import com.postres.dto.ProductResponseDTO;
import com.postres.dto.ProductoDTO;
import com.postres.dto.ProductoRequestDTO;
import com.postres.entity.Producto;
import com.postres.service.base.GenericService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService extends GenericService<Producto, ProductoDTO,Long> {
    List<ProductResponseDTO> findAllProductos();
    ProductResponseDTO findProductById(Long id);
    ProductoDTO createWithImage(ProductoRequestDTO productoRequest, MultipartFile file);
}
