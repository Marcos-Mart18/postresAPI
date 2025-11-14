package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.PedidoDTO;
import com.postres.entity.Estado;
import com.postres.entity.DetallePedido;
import com.postres.entity.Pedido;
import com.postres.entity.Producto;
import com.postres.entity.Usuario;
import com.postres.mappers.PedidoMapper;
import com.postres.repository.EstadoRepository;
import com.postres.repository.PedidoRepository;
import com.postres.repository.DetallePedidoRepository;
import com.postres.repository.ProductoRepository;
import com.postres.repository.UsuarioRepository;
import com.postres.service.service.PedidoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,UsuarioRepository usuarioRepository,EstadoRepository estadoRepository,PedidoMapper pedidoMapper, DetallePedidoRepository detallePedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.pedidoMapper = pedidoMapper;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
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
    @Transactional
    public PedidoDTO create(PedidoDTO pedidoDTO) throws ServiceException {
        try {
            Pedido pedido = new Pedido();
            pedido.setFechaEntrega(pedidoDTO.getFechaEntrega());
            pedido.setHoraEntrega(pedidoDTO.getHoraEntrega());
            pedido.setDireccion(pedidoDTO.getDireccion());

            String numeroOrden;
            do {
                numeroOrden = generarNumeroOrden();
            } while (!validarNumeroOrdenUnico(numeroOrden));
            pedido.setNumOrden(numeroOrden);

            if (pedidoDTO.getIdEstado() != null) {
                Estado estado = estadoRepository.findById(pedidoDTO.getIdEstado())
                        .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado"));
                pedido.setEstado(estado);
            }

            // Persistimos el pedido primero para obtener su ID
            Pedido saved = pedidoRepository.save(pedido);

            // Crear detalles y calcular total si vienen en el DTO
            Double totalCalculado = 0.0;
            if (pedidoDTO.getDetalles() != null && !pedidoDTO.getDetalles().isEmpty()) {
                for (var detDto : pedidoDTO.getDetalles()) {
                    Producto producto = productoRepository.findById(detDto.getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detDto.getCantidad());
                    detalle.setPedido(saved);
                    detalle.setProducto(producto);
                    detallePedidoRepository.save(detalle);
                    totalCalculado += producto.getPrecio() * detDto.getCantidad();
                }
                saved.setTotal(totalCalculado);
                saved = pedidoRepository.save(saved);
            } else {
                // Si no hay detalles, usar el total proporcionado (fallback)
                saved.setTotal(pedidoDTO.getTotal());
                saved = pedidoRepository.save(saved);
            }

            return pedidoMapper.toDTO(saved);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al crear el pedido", e);
        }
    }

    @Override
    @Transactional
    public PedidoDTO update(Long aLong, PedidoDTO pedidoDTO) throws ServiceException {
        try {
            Pedido pedido = pedidoRepository.findById(aLong)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

            pedido.setFechaEntrega(pedidoDTO.getFechaEntrega());
            pedido.setHoraEntrega(pedidoDTO.getHoraEntrega());
            pedido.setDireccion(pedidoDTO.getDireccion());

            if (pedidoDTO.getIdEstado() != null) {
                Estado estado = estadoRepository.findById(pedidoDTO.getIdEstado())
                        .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado"));
                pedido.setEstado(estado);
            }
            // Si el update trae detalles, reemplazamos los existentes y recalculamos total
            if (pedidoDTO.getDetalles() != null) {
                List<DetallePedido> existentes = pedido.getDetallePedidos();
                if (existentes != null && !existentes.isEmpty()) {
                    detallePedidoRepository.deleteAll(existentes);
                }
                Double totalCalculado = 0.0;
                for (var detDto : pedidoDTO.getDetalles()) {
                    Producto producto = productoRepository.findById(detDto.getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detDto.getCantidad());
                    detalle.setPedido(pedido);
                    detalle.setProducto(producto);
                    detallePedidoRepository.save(detalle);
                    totalCalculado += producto.getPrecio() * detDto.getCantidad();
                }
                pedido.setTotal(totalCalculado);
            } else {
                // Si no vienen detalles, permitimos actualizar otros campos sin tocar el total
                pedido.setTotal(pedidoDTO.getTotal());
            }

            Pedido updated = pedidoRepository.save(pedido);
            return pedidoMapper.toDTO(updated);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar el pedido", e);
        }
    }

    @Override
    public PedidoDTO findById(Long aLong) throws ServiceException {
        try {
            Pedido pedido = pedidoRepository.findById(aLong)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
            return pedidoMapper.toDTO(pedido);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al leer el pedido con id " + aLong, e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long aLong) throws ServiceException {
        try {
            if(!pedidoRepository.findById(aLong).isPresent()){
                throw new ResourceNotFoundException("Pedido no encontrado");
            }
            pedidoRepository.deleteById(aLong);
        }catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al eliminar el pedido con id " + aLong, e);
        }
    }

    @Override
    public List<PedidoDTO> findAll() throws ServiceException {
        try {
            List<Pedido> pedidos = pedidoRepository.findAll();
            return pedidoMapper.toDTOs(pedidos);
        } catch (Exception e) {
            throw new ServiceException("Error al listar los pedidos", e);
        }
    }

    @Override
    @Transactional
    public PedidoDTO createByUser(PedidoDTO pedidoDTO,String username) {
        try {

            // Buscar el usuario por el username
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear la entidad Pedido a partir del DTO
            Pedido pedido = new Pedido();
            pedido.setFechaEntrega(pedidoDTO.getFechaEntrega());  // Fecha solo
            pedido.setHoraEntrega(pedidoDTO.getHoraEntrega());    // Hora solo
            String numeroOrden;
            do {
                numeroOrden = generarNumeroOrden();
            } while (!validarNumeroOrdenUnico(numeroOrden));
            pedido.setNumOrden(numeroOrden);
            pedido.setDireccion(pedidoDTO.getDireccion());

            // Asignar el estado del pedido
            Estado estado = estadoRepository.findById(pedidoDTO.getIdEstado())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            pedido.setEstado(estado);

            // Asignar el usuario al pedido
            pedido.setUsuario(usuario);

            // Guardar pedido para obtener ID
            Pedido pedidoSaved = pedidoRepository.save(pedido);

            // Crear detalles y calcular total si vienen en el DTO
            Double totalCalculado = 0.0;
            if (pedidoDTO.getDetalles() != null && !pedidoDTO.getDetalles().isEmpty()) {
                for (var detDto : pedidoDTO.getDetalles()) {
                    Producto producto = productoRepository.findById(detDto.getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detDto.getCantidad());
                    detalle.setPedido(pedidoSaved);
                    detalle.setProducto(producto);
                    detallePedidoRepository.save(detalle);
                    totalCalculado += producto.getPrecio() * detDto.getCantidad();
                }
                pedidoSaved.setTotal(totalCalculado);
                pedidoSaved = pedidoRepository.save(pedidoSaved);
            } else {
                // Fallback si no hay detalles
                pedidoSaved.setTotal(pedidoDTO.getTotal());
                pedidoSaved = pedidoRepository.save(pedidoSaved);
            }

            // Retornar el DTO de respuesta
            return pedidoMapper.toDTO(pedidoSaved);
        } catch (Exception e) {
            throw new ServiceException("Error al procesar la solicitud: " + e.getMessage(), e);
        }
    }

}
