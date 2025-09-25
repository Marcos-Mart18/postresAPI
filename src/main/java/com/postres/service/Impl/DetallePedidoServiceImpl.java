package com.postres.service.Impl;

import com.postres.controller.exceptions.ResourceNotFoundException;
import com.postres.dto.DetallePedidoDTO;
import com.postres.entity.DetallePedido;
import com.postres.entity.Pedido;
import com.postres.entity.Producto;
import com.postres.mappers.DetallePedidoMapper;
import com.postres.mappers.EstadoMapper;
import com.postres.repository.DetallePedidoRepository;
import com.postres.repository.EstadoRepository;
import com.postres.repository.PedidoRepository;
import com.postres.repository.ProductoRepository;
import com.postres.service.service.DetallePedidoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {
    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoMapper detallePedidoMapper;

    public DetallePedidoServiceImpl(DetallePedidoRepository detallePedidoRepository,PedidoRepository pedidoRepository,ProductoRepository productoRepository, DetallePedidoMapper detallePedidoMapper) {
        this.detallePedidoRepository = detallePedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.detallePedidoMapper = detallePedidoMapper;
    }

    @Override
    public DetallePedidoDTO create(DetallePedidoDTO detallePedidoDTO) throws ServiceException {
        try {
            // Buscar el pedido
            Pedido pedido = pedidoRepository.findById(detallePedidoDTO.getIdPedido())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

            // Buscar el producto
            Producto producto = productoRepository.findById(detallePedidoDTO.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            // Crear entidad DetallePedido
            DetallePedido detallePedido = new DetallePedido();
            detallePedido.setPedido(pedido);
            detallePedido.setProducto(producto);
            detallePedido.setCantidad(detallePedidoDTO.getCantidad());
            detallePedido.setIsActive('A'); // Activo por defecto

            // Guardar en la BD
            DetallePedido detalleGuardado = detallePedidoRepository.save(detallePedido);

            // Retornar DTO
            return detallePedidoMapper.toDTO(detalleGuardado);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al crear detalle de pedido", e);
        }
    }

    @Override
    public DetallePedidoDTO update(Long aLong, DetallePedidoDTO detallePedidoDTO) throws ServiceException {
        return null;
    }

    @Override
    public DetallePedidoDTO findById(Long aLong) throws ServiceException {
        return null;
    }

    @Override
    public void deleteById(Long aLong) throws ServiceException {
        try {
            if(!detallePedidoRepository.findById(aLong).isPresent()){
                throw new ResourceNotFoundException("Categoria no encontrada");
            }
            detallePedidoRepository.deleteById(aLong);
        }catch (ResourceNotFoundException e) {
            throw (e);
        }catch (Exception e) {
            throw new ServiceException("Error al eliminar la categoría con id " + aLong, e);
        }
    }

    @Override
    public List<DetallePedidoDTO> findAll() throws ServiceException {
        return List.of();
    }
}
