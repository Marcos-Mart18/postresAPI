package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.PedidoDTO;
import com.postres.entity.Estado;
import com.postres.entity.Pedido;
import com.postres.entity.Usuario;
import com.postres.mappers.PedidoMapper;
import com.postres.repository.EstadoRepository;
import com.postres.repository.PedidoRepository;
import com.postres.repository.UsuarioRepository;
import com.postres.service.service.PedidoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;
    private final PedidoMapper pedidoMapper;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,UsuarioRepository usuarioRepository,EstadoRepository estadoRepository,PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    // Método para generar el número de orden
    public String generarNumeroOrden() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String fecha = dateFormat.format(new Date());

        Random random = new Random();
        int numeroAleatorio = 1000 + random.nextInt(9000);

        return "ORD-" + fecha + "-" + numeroAleatorio;
    }

    // Método para validar que el número de orden no esté duplicado
    public boolean validarNumeroOrdenUnico(String numeroOrden) {
        // Aquí puedes verificar en la base de datos si el número de orden ya existe
        return !pedidoRepository.existsByNumOrden(numeroOrden);  // Método que busca en la base de datos
    }


    @Override
    public PedidoDTO create(PedidoDTO pedidoDTO) throws ServiceException {
        return null;
    }

    @Override
    public PedidoDTO update(Long aLong, PedidoDTO pedidoDTO) throws ServiceException {
        return null;
    }

    @Override
    public PedidoDTO findById(Long aLong) throws ServiceException {
        return null;
    }

    @Override
    public void deleteById(Long aLong) throws ServiceException {
        try {
            if(!pedidoRepository.findById(aLong).isPresent()){
                throw new ResourceNotFoundException("Categoria no encontrada");
            }
            pedidoRepository.deleteById(aLong);
        }catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al eliminar la categoría con id " + aLong, e);
        }
    }

    @Override
    public List<PedidoDTO> findAll() throws ServiceException {
        return List.of();
    }

    @Override
    public PedidoDTO createByUser(PedidoDTO pedidoDTO,String username) {
        try {

            // Buscar el usuario por el username
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear la entidad Pedido a partir del DTO
            Pedido pedido = new Pedido();
            pedido.setFechaEntrega(pedidoDTO.getFechaEntrega());  // Fecha solo
            pedido.setHoraEntrega(pedidoDTO.getHoraEntrega());    // Hora solo
            pedido.setTotal(pedidoDTO.getTotal());
            pedido.setNumOrden(generarNumeroOrden());
            pedido.setDireccion(pedidoDTO.getDireccion());

            // Asignar el estado del pedido
            Estado estado = estadoRepository.findById(pedidoDTO.getIdEstado())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            pedido.setEstado(estado);

            // Asignar el usuario al pedido
            pedido.setUsuario(usuario);

            // Guardar el pedido en la base de datos
            Pedido pedidoSaved = pedidoRepository.save(pedido);

            // Retornar el DTO de respuesta
            return pedidoMapper.toDTO(pedidoSaved);
        } catch (Exception e) {
            throw new ServiceException("Error al procesar la solicitud: " + e.getMessage(), e);
        }
    }

}
