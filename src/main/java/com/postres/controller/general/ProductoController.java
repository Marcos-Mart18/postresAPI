package com.postres.controller.general;

import com.postres.dto.ProductoDTO;
import com.postres.entity.Producto;
import com.postres.service.Impl.CloudinaryService;
import com.postres.service.service.ProductoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
    private final ProductoService productoService;
    private final CloudinaryService cloudinaryService;

    public ProductoController(ProductoService productoService,CloudinaryService cloudinaryService) {
        this.productoService = productoService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listAll() throws ServiceException {

        return ResponseEntity.ok(productoService.findAllProductos());
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadImage/{productoId}")
    public String uploadImage(@PathVariable Long productoId, @RequestParam("file") MultipartFile file) {
        try {
            // Subir la imagen y obtener la URL
            String imageUrl = cloudinaryService.uploadImage(file, productoId);

            return "Imagen subida correctamente. URL: " + imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al subir la imagen: " + e.getMessage();
        }
    }

}
