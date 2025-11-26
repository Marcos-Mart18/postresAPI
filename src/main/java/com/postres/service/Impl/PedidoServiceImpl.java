package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.PedidoDTO;
import com.postres.dto.PedidoResumenDTO;
import com.postres.dto.PedidoDetalleViewDTO;
import com.postres.dto.EstadoDTO;
import com.postres.entity.Estado;
import com.postres.entity.Repartidor;
import com.postres.entity.DetallePedido;
import com.postres.entity.Pedido;
import com.postres.entity.Producto;
import com.postres.entity.Usuario;
import com.postres.mappers.PedidoMapper;
import com.postres.mappers.EstadoMapper;
import com.postres.mappers.DetallePedidoMapper;
import com.postres.repository.EstadoRepository;
import com.postres.repository.PedidoRepository;
import com.postres.repository.DetallePedidoRepository;
import com.postres.repository.ProductoRepository;
import com.postres.repository.UsuarioRepository;
import com.postres.repository.RepartidorRepository;
import com.postres.service.service.PedidoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.time.LocalDate;

@Service
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;
    private final PedidoMapper pedidoMapper;
    private final EstadoMapper estadoMapper;
    private final DetallePedidoMapper detallePedidoMapper;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final RepartidorRepository repartidorRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,UsuarioRepository usuarioRepository,EstadoRepository estadoRepository,PedidoMapper pedidoMapper, EstadoMapper estadoMapper, DetallePedidoMapper detallePedidoMapper, DetallePedidoRepository detallePedidoRepository, ProductoRepository productoRepository, RepartidorRepository repartidorRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.pedidoMapper = pedidoMapper;
        this.estadoMapper = estadoMapper;
        this.detallePedidoMapper = detallePedidoMapper;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.repartidorRepository = repartidorRepository;
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
        // Nueva lógica: el repartidor solo puede cambiar a EN_CAMINO o ENTREGADO, y debe estar asignado
        if (pedido.getRepartidor() == null || repartidor.getRepartidor() == null ||
                !pedido.getRepartidor().getIdRepartidor().equals(repartidor.getRepartidor().getIdRepartidor())) {
            throw new ServiceException("El repartidor no está asignado a este pedido");
        }

        String nuevoEstado = estado.getNombre().toUpperCase();
        if (!("EN CAMINO".equalsIgnoreCase(nuevoEstado) || "EN_CAMINO".equalsIgnoreCase(nuevoEstado)
                || "ENTREGADO".equalsIgnoreCase(nuevoEstado))) {
            throw new ServiceException("El repartidor solo puede actualizar a EN_CAMINO o ENTREGADO");
        }

        pedido.setEstado(estado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        PedidoDTO dto = pedidoMapper.toDTO(pedidoActualizado);
        return dto;
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

    private Estado obtenerEstadoPorNombre(String nombre) {
        return estadoRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado: " + nombre));
    }

    private void validarCupoDiario(LocalDate fechaEntrega, long cantidadSolicitada) {
        Long sumActual = detallePedidoRepository.sumCantidadByFechaEntregaExcluyendoCancelado(fechaEntrega);
        long totalConNuevo = (sumActual == null ? 0L : sumActual) + cantidadSolicitada;
        if (totalConNuevo > 25L) {
            throw new ServiceException("No hay cupo disponible para la fecha seleccionada. Cupo diario máximo: 25");
        }
    }

    @Override
    @Transactional
    public PedidoDTO create(PedidoDTO pedidoDTO) throws ServiceException {
        try {
            Pedido pedido = new Pedido();
            pedido.setFechaEntrega(pedidoDTO.getFechaEntrega());
            pedido.setHoraEntrega(pedidoDTO.getHoraEntrega());
            pedido.setDireccion(pedidoDTO.getDireccion());

            // Validaciones de fecha: no el mismo día (ADMIN también debe respetar la regla)
            if (!pedido.getFechaEntrega().isAfter(LocalDate.now())) {
                throw new ServiceException("La fecha de entrega debe ser al menos con un día de anticipación");
            }

            String numeroOrden;
            do {
                numeroOrden = generarNumeroOrden();
            } while (!validarNumeroOrdenUnico(numeroOrden));
            pedido.setNumOrden(numeroOrden);

            // Estado por defecto: PENDIENTE
            Estado estadoPendiente = obtenerEstadoPorNombre("PENDIENTE");
            pedido.setEstado(estadoPendiente);

            // Persistimos el pedido primero para obtener su ID
            Pedido saved = pedidoRepository.save(pedido);

            // Crear detalles y calcular total si vienen en el DTO
            Double totalCalculado = 0.0;
            long cantidadSolicitada = 0L;
            if (pedidoDTO.getDetalles() != null && !pedidoDTO.getDetalles().isEmpty()) {
                for (var detDto : pedidoDTO.getDetalles()) {
                    Producto producto = productoRepository.findById(detDto.getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detDto.getCantidad());
                    detalle.setPedido(saved);
                    detalle.setProducto(producto);
                    detalle.setPrecioUnitario(producto.getPrecio());
                    detallePedidoRepository.save(detalle);
                    totalCalculado += producto.getPrecio() * detDto.getCantidad();
                    cantidadSolicitada += detDto.getCantidad();
                }
                // Validar cupo diario
                validarCupoDiario(saved.getFechaEntrega(), cantidadSolicitada);
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

            if (pedidoDTO.getFechaEntrega() != null) {
                if (!pedidoDTO.getFechaEntrega().isAfter(LocalDate.now())) {
                    throw new ServiceException("La fecha de entrega debe ser al menos con un día de anticipación");
                }
                pedido.setFechaEntrega(pedidoDTO.getFechaEntrega());
            }
            pedido.setHoraEntrega(pedidoDTO.getHoraEntrega());
            pedido.setDireccion(pedidoDTO.getDireccion());

            if (pedidoDTO.getEstado() != null && pedidoDTO.getEstado().getIdEstado() != null) {
                Estado estado = estadoRepository.findById(pedidoDTO.getEstado().getIdEstado())
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
                    detalle.setPrecioUnitario(producto.getPrecio());
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

            // Validaciones de fecha: no el mismo día (ya garantizado por @Future), pero reforzamos
            if (!pedido.getFechaEntrega().isAfter(LocalDate.now())) {
                throw new ServiceException("La fecha de entrega debe ser al menos con un día de anticipación");
            }

            // Generar número de orden único
            String numeroOrden;
            do {
                numeroOrden = generarNumeroOrden();
            } while (!validarNumeroOrdenUnico(numeroOrden));
            pedido.setNumOrden(numeroOrden);

            // Asignar estado por defecto PENDIENTE
            Estado estado = obtenerEstadoPorNombre("PENDIENTE");
            pedido.setEstado(estado);

            // Asignar el usuario al pedido
            pedido.setUsuario(usuario);

            // Guardar el pedido primero para obtener el ID
            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            // Procesar los detalles del pedido
            Double total = 0.0;
            long cantidadSolicitada = 0L;
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
                    cantidadSolicitada += detalleDTO.getCantidad();
                }

                // Validar cupo diario
                validarCupoDiario(pedidoGuardado.getFechaEntrega(), cantidadSolicitada);

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

    private String obtenerNombreRepartidor(Repartidor repartidor) {
        if (repartidor == null || repartidor.getUsuarios() == null) return null;
        for (Usuario u : repartidor.getUsuarios()) {
            if (u.getIsActive() == 'A' && u.getPersona() != null) {
                return (u.getPersona().getNombres() + " " + u.getPersona().getApellidos()).trim();
            }
        }
        return null;
    }

    private PedidoResumenDTO toResumenDTO(Pedido p) {
        EstadoDTO est = p.getEstado() != null ? estadoMapper.toDTO(p.getEstado()) : null;
        return new PedidoResumenDTO(
                p.getIdPedido(),
                p.getFechaPedido(),
                p.getFechaEntrega(),
                p.getTotal(),
                est,
                obtenerNombreRepartidor(p.getRepartidor())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResumenDTO> obtenerResumenPorRepartidor(String usernameRepartidor) {
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
        
        // Convertir a resumen DTOs y retornar
        return pedidos.stream().map(this::toResumenDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResumenDTO> obtenerResumenPorCliente(String usernameCliente) {
        // Buscar el cliente por username
        Usuario cliente = usuarioRepository.findByUsername(usernameCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con username: " + usernameCliente));
        
        // Obtener los pedidos del cliente
        List<Pedido> pedidos = pedidoRepository.findByUsuario(cliente);
        
        // Convertir a resumen DTOs y retornar
        return pedidos.stream().map(this::toResumenDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResumenDTO> listarResumenGeneral() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream().map(this::toResumenDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDetalleViewDTO obtenerDetallePedido(Long idPedido) {
        Pedido p = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        EstadoDTO est = p.getEstado() != null ? estadoMapper.toDTO(p.getEstado()) : null;

        return new PedidoDetalleViewDTO(
                p.getIdPedido(),
                p.getNumOrden(),
                p.getFechaPedido(),
                p.getFechaEntrega(),
                p.getHoraEntrega(),
                p.getTotal(),
                p.getDireccion(),
                est,
                obtenerNombreRepartidor(p.getRepartidor()),
                detallePedidoMapper.toDTOs(p.getDetallePedidos())
        );
    }

    // Admin operations
    @Override
    @Transactional
    public PedidoDTO aceptarPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        Estado estado = obtenerEstadoPorNombre("ACEPTADO");
        pedido.setEstado(estado);
        Pedido saved = pedidoRepository.save(pedido);
        PedidoDTO dto = pedidoMapper.toDTO(saved);
        return dto;
    }

    @Override
    @Transactional
    public PedidoDTO marcarEnPreparacion(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        Estado estado = obtenerEstadoPorNombre("EN_PREPARACION");
        pedido.setEstado(estado);
        Pedido saved = pedidoRepository.save(pedido);
        PedidoDTO dto = pedidoMapper.toDTO(saved);
        return dto;
    }

    @Override
    @Transactional
    public PedidoDTO marcarListoParaEntrega(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        Estado estado = obtenerEstadoPorNombre("LISTO_PARA_ENTREGA");
        pedido.setEstado(estado);
        Pedido saved = pedidoRepository.save(pedido);
        PedidoDTO dto = pedidoMapper.toDTO(saved);
        return dto;
    }

    @Override
    @Transactional
    public PedidoDTO asignarRepartidor(Long idPedido, Long idRepartidor) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado"));
        Estado estado = obtenerEstadoPorNombre("ASIGNADO");
        pedido.setRepartidor(repartidor);
        pedido.setEstado(estado);
        Pedido saved = pedidoRepository.save(pedido);
        PedidoDTO dto = pedidoMapper.toDTO(saved);
        return dto;
    }

    @Override
    @Transactional
    public PedidoDTO cancelarPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        // Bloquear cancelación si ya está ENTREGADO
        if (pedido.getEstado() != null && "ENTREGADO".equalsIgnoreCase(pedido.getEstado().getNombre())) {
            throw new ServiceException("No se puede cancelar un pedido que ya fue ENTREGADO");
        }
        Estado estado = obtenerEstadoPorNombre("CANCELADO");
        pedido.setEstado(estado);
        Pedido saved = pedidoRepository.save(pedido);
        PedidoDTO dto = pedidoMapper.toDTO(saved);
        return dto;
    }

    // Repartidor operations
    @Override
    @Transactional
    public PedidoDTO iniciarEntrega(Long idPedido, String usernameRepartidor) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        Usuario repartidorUsuario = usuarioRepository.findByUsername(usernameRepartidor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (pedido.getRepartidor() == null || repartidorUsuario.getRepartidor() == null ||
                !pedido.getRepartidor().getIdRepartidor().equals(repartidorUsuario.getRepartidor().getIdRepartidor())) {
            throw new ServiceException("El repartidor no está asignado a este pedido");
        }
        Estado estado = obtenerEstadoPorNombre("EN_CAMINO");
        pedido.setEstado(estado);
        Pedido saved = pedidoRepository.save(pedido);
        PedidoDTO dto = pedidoMapper.toDTO(saved);
        return dto;
    }

    @Override
    @Transactional
    public PedidoDTO marcarEntregado(Long idPedido, String usernameRepartidor) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        Usuario repartidorUsuario = usuarioRepository.findByUsername(usernameRepartidor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (pedido.getRepartidor() == null || repartidorUsuario.getRepartidor() == null ||
                !pedido.getRepartidor().getIdRepartidor().equals(repartidorUsuario.getRepartidor().getIdRepartidor())) {
            throw new ServiceException("El repartidor no está asignado a este pedido");
        }
        Estado estado = obtenerEstadoPorNombre("ENTREGADO");
        pedido.setEstado(estado);
        Pedido saved = pedidoRepository.save(pedido);
        PedidoDTO dto = pedidoMapper.toDTO(saved);
        return dto;
    }

    @Transactional
    public PedidoDTO agregarDetalles(Long idPedido, List<com.postres.dto.DetallePedidoDTO> nuevos, String username) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        // Validar pertenencia (cliente dueño del pedido)
        if (pedido.getUsuario() == null || !pedido.getUsuario().getUsername().equals(username)) {
            throw new ServiceException("No tienes permisos para modificar este pedido");
        }

        // Validar fecha: aún en el futuro
        if (pedido.getFechaEntrega() == null || !pedido.getFechaEntrega().isAfter(LocalDate.now())) {
            throw new ServiceException("Solo puedes modificar pedidos con fecha de entrega futura");
        }

        // Validar estado: no permitir si ENTREGADO o CANCELADO
        if (pedido.getEstado() != null) {
            String nom = pedido.getEstado().getNombre();
            if ("ENTREGADO".equalsIgnoreCase(nom) || "CANCELADO".equalsIgnoreCase(nom)) {
                throw new ServiceException("No se pueden agregar productos a un pedido entregado o cancelado");
            }
        }

        // Validar cupo diario solo con lo nuevo
        long cantidadNueva = 0L;
        Double incrementoTotal = 0.0;
        for (var detDto : nuevos) {
            Producto producto = productoRepository.findById(detDto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            DetallePedido det = new DetallePedido();
            det.setPedido(pedido);
            det.setProducto(producto);
            det.setCantidad(detDto.getCantidad());
            det.setPrecioUnitario(producto.getPrecio());
            detallePedidoRepository.save(det);
            cantidadNueva += detDto.getCantidad();
            incrementoTotal += producto.getPrecio() * detDto.getCantidad();
        }

        validarCupoDiario(pedido.getFechaEntrega(), cantidadNueva);

        pedido.setTotal((pedido.getTotal() == null ? 0.0 : pedido.getTotal()) + incrementoTotal);
        Pedido actualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(actualizado);
    }
}
