package com.postres.controller.general;

import com.postres.dto.ProductoDTO;
import com.postres.service.service.ProductoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {

        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listAll() throws ServiceException {

        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> listById(@PathVariable Long id) throws ServiceException {
        ProductoDTO productoDTO  = productoService.findById(id);
        return ResponseEntity.ok(productoDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductoDTO> create(@RequestBody ProductoDTO productoDTO) throws ServiceException {
        ProductoDTO productoDTO1 = productoService.create(productoDTO);
        return new ResponseEntity<>(productoDTO1, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> update(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) throws ServiceException {
        ProductoDTO productoDTO1 = productoService.update(id,productoDTO);
        return ResponseEntity.ok(productoDTO1);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ServiceException {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
