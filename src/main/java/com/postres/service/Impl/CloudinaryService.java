package com.postres.service.Impl;

import com.cloudinary.Cloudinary;
import com.postres.entity.Producto;
import com.postres.entity.Usuario;
import com.postres.repository.ProductoRepository;
import com.postres.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;


import java.io.IOException;
import java.util.Map;
@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
                             @Value("${cloudinary.api-key}") String apiKey,
                             @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    public String uploadImage(MultipartFile file, Long productoId) throws IOException {
        // Parámetros de configuración para la subida
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", "productos",  // Carpeta donde se subirá la imagen
                "public_id", productoId.toString()  // Opcional: puedes asignar un nombre único basado en el productoId
        );

        // Subir la imagen a Cloudinary dentro de la carpeta "productos"
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        // Obtener la URL de la imagen subida
        String imageUrl = (String) uploadResult.get("url");

        // Actualizar el producto con la URL de la imagen
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setFotoUrl(imageUrl);  // Establecer la URL de la imagen en el campo fotoUrl
        productoRepository.save(producto);  // Guardar el producto con la URL actualizada

        return imageUrl;  // Retornar la URL de la imagen subida
    }


    public String uploadProfileImage(MultipartFile file, Long usuarioId) throws IOException {
        // Parámetros de configuración para la subida
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", "profilePics",  // Carpeta donde se subirá la imagen
                "public_id", usuarioId.toString()  // Usar el ID del usuario como nombre único para la imagen
        );

        // Subir la imagen a Cloudinary dentro de la carpeta "profilePics"
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        // Obtener la URL de la imagen subida
        String imageUrl = (String) uploadResult.get("url");

        // Actualizar el usuario con la URL de la imagen
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setProfileFotoUrl(imageUrl);  // Establecer la URL de la imagen en el campo profileFotoUrl
        usuarioRepository.save(usuario);  // Guardar el usuario con la URL actualizada

        return imageUrl;  // Retornar la URL de la imagen subida
    }

}
