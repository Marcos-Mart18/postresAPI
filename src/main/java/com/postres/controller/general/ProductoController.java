package com.postres.controller.general;

import com.postres.controller.exceptions.BusinessException;
import com.postres.dto.ProductResponseDTO;
import com.postres.dto.ProductoDTO;
import com.postres.dto.ProductoRequestDTO;
import com.postres.service.Impl.CloudinaryService;
import com.postres.service.service.ProductoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png");
    
    private final ProductoService productoService;
    private final CloudinaryService cloudinaryService;

    public ProductoController(ProductoService productoService, CloudinaryService cloudinaryService) {
        this.productoService = productoService;
        this.cloudinaryService = cloudinaryService;
    }
    
    private void validateImageFile(MultipartFile file) {
        // Validar tamaño del archivo
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("El tamaño de la imagen no debe superar los 10MB");
        }
        
        // Validar tipo de contenido
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("Solo se permiten archivos de imagen JPG o PNG");
        }
        
        // Validar extensión del archivo
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("Nombre de archivo no válido");
        }
        
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new BusinessException("La extensión del archivo no es válida. Use .jpg, .jpeg o .png");
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> listAll() throws ServiceException {

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
    public ResponseEntity<String> uploadImage(
            @PathVariable Long productoId, 
            @RequestParam("file") MultipartFile file) {
        
        validateImageFile(file);
        
        try {
            String imageUrl = cloudinaryService.uploadImage(file, productoId);
            return ResponseEntity.ok("Imagen subida correctamente. URL: " + imageUrl);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error al procesar la imagen: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/createWithImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProductoWithImage(
            @RequestPart("producto") ProductoRequestDTO productoRequestDTO,
            @RequestPart("file") MultipartFile file) {
            
        validateImageFile(file);
        
        try {
            ProductoDTO productoCreado = productoService.createWithImage(productoRequestDTO, file);
            return new ResponseEntity<>(productoCreado, HttpStatus.CREATED);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error al crear el producto con imagen: " + e.getMessage());
        }
    }





}
