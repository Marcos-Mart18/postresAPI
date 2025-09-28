package com.postres.service.service;

import com.postres.dto.ProductResponseDTO;
import com.postres.dto.ProductoDTO;
import com.postres.entity.Producto;
import com.postres.service.base.GenericService;

import java.util.List;

public interface ProductoService extends GenericService<Producto, ProductoDTO,Long> {
    List<ProductResponseDTO> findAllProductos();
}
