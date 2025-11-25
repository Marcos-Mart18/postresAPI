package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.PedidoDTO;
import com.postres.entity.Estado;
import com.postres.entity.Repartidor;
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

    @Override
    @Transactional
    public PedidoDTO actualizarEstado(Long idPedido, Long idEstado, String usernameRepartidor) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + idPedido));
        
        Estado estado = estadoRepository.findById(idEstado)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con id: " + idEstado));
        
        Usuario repartidor = usuarioRepository.findByUsername(usernameRepartidor)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado con username: " + usernameRepartidor));
        
        // Verificar que el usuario sea un repartidor
        boolean isRepartidor = repartidor.getUsuarioRoles().stream()
                .anyMatch(usuarioRol -> usuarioRol.getRol().getNombre().equals("REPARTIDOR"));
                
        if (!isRepartidor) {
            throw new ServiceException("El usuario no tiene permisos de repartidor");
        }
        
        // Actualizar el estado del pedido y el repartidor asignado
        pedido.setEstado(estado);
        pedido.setRepartidor(repartidor.getRepartidor());
        
        // Guardar los cambios
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        return pedidoMapper.toDTO(pedidoActualizado);
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
    public PedidoDTO createByUser(PedidoDTO pedidoDTO, String username) {
        try {
            // Obtener el usuario que hace el pedido
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Crear el pedido
            Pedido pedido = new Pedido();
            pedido.setFechaEntrega(pedidoDTO.getFechaEntrega());
            pedido.setHoraEntrega(pedidoDTO.getHoraEntrega());
            pedido.setDireccion(pedidoDTO.getDireccion());

            // Generar número de orden único
            String numeroOrden;
            do {
                numeroOrden = generarNumeroOrden();
            } while (!validarNumeroOrdenUnico(numeroOrden));
            pedido.setNumOrden(numeroOrden);

            // Asignar estado por defecto (Pendiente - ID 1)
            Estado estado = estadoRepository.findById(1L)
                    .orElseThrow(() -> new ResourceNotFoundException("Estado por defecto no encontrado"));
            pedido.setEstado(estado);

            // Asignar el usuario al pedido
            pedido.setUsuario(usuario);

            // Guardar el pedido primero para obtener el ID
            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            // Procesar los detalles del pedido
            Double total = 0.0;
            if (pedidoDTO.getDetalles() != null && !pedidoDTO.getDetalles().isEmpty()) {
                for (var detalleDTO : pedidoDTO.getDetalles()) {
                    Producto producto = productoRepository.findById(detalleDTO.getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDTO.getIdProducto()));

                    DetallePedido detalle = new DetallePedido();
                    detalle.setPedido(pedidoGuardado);
                    detalle.setProducto(producto);
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setPrecioUnitario(producto.getPrecio());

                    detallePedidoRepository.save(detalle);
                    total += producto.getPrecio() * detalleDTO.getCantidad();
                }

                // Actualizar el total del pedido
                pedidoGuardado.setTotal(total);
                pedidoRepository.save(pedidoGuardado);
            }

            return pedidoMapper.toDTO(pedidoGuardado);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al crear el pedido: " + e.getMessage(), e);
        }
    }

    @Transactional

    /**
     * Valida si la transición entre estados es válida
     */
    private boolean esTransicionValida(Long estadoActualId, Long nuevoEstadoId) {
        // Implementa aquí la lógica de validación de transiciones de estado
        // Por ejemplo, podrías definir qué transiciones son permitidas

        // Ejemplo básico: solo permitir transiciones secuenciales
        // (1->2, 2->3, 3->4, etc.)
        return nuevoEstadoId > estadoActualId || 
               (estadoActualId.equals(5L) && nuevoEstadoId.equals(5L)); // Permitir cancelación (estado 5) desde cualquier estado
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPedidosPorRepartidor(String usernameRepartidor) {
        // Buscar el repartidor por username
        Usuario repartidorUsuario = usuarioRepository.findByUsername(usernameRepartidor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario repartidor no encontrado con username: " + usernameRepartidor));
        
        // Verificar que el usuario sea un repartidor
        boolean isRepartidor = repartidorUsuario.getUsuarioRoles().stream()
                .anyMatch(usuarioRol -> usuarioRol.getRol().getNombre().equals("REPARTIDOR"));
                
        if (!isRepartidor) {
            throw new ServiceException("El usuario no tiene permisos de repartidor");
        }
        
        // Obtener el objeto Repartidor asociado al usuario
        Repartidor repartidor = repartidorUsuario.getRepartidor();
        if (repartidor == null) {
            throw new ServiceException("No se encontró información de repartidor para el usuario");
        }
        
        // Obtener los pedidos asignados al repartidor
        List<Pedido> pedidos = pedidoRepository.findByRepartidor(repartidor);
        
        // Convertir a DTOs y retornar
        return pedidoMapper.toDTOs(pedidos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPedidosPorCliente(String usernameCliente) {
        // Buscar el cliente por username
        Usuario cliente = usuarioRepository.findByUsername(usernameCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con username: " + usernameCliente));
        
        // Obtener los pedidos del cliente
        List<Pedido> pedidos = pedidoRepository.findByUsuario(cliente);
        
        // Convertir a DTOs y retornar
        return pedidoMapper.toDTOs(pedidos);
    }
}
