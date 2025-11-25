package com.postres.controller.general;

import com.postres.dto.PedidoDTO;
import com.postres.service.service.PedidoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {

        this.pedidoService = pedidoService;
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/create")
    public ResponseEntity<PedidoDTO> createByUser(@RequestBody @Valid PedidoDTO pedidoDTO, Authentication authentication) {
        try {
            String username = authentication.getName();

            PedidoDTO pedidoCreado = pedidoService.createByUser(pedidoDTO, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCreado);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listAll() throws ServiceException {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> listById(@PathVariable Long id) throws ServiceException {
        PedidoDTO pedidoDTO = pedidoService.findById(id);
        return ResponseEntity.ok(pedidoDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PedidoDTO> create(@RequestBody @Valid PedidoDTO pedidoDTO) throws ServiceException {
        PedidoDTO created = pedidoService.create(pedidoDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> update(@PathVariable Long id, @RequestBody @Valid PedidoDTO pedidoDTO) throws ServiceException {
        PedidoDTO updated = pedidoService.update(id, pedidoDTO);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ServiceException {
        pedidoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('REPARTIDOR')")
    @PutMapping("/{id}/estado/{idEstado}")
    public ResponseEntity<PedidoDTO> actualizarEstadoPedido(
            @PathVariable Long id,
            @PathVariable Long idEstado,
            Authentication authentication) throws ServiceException {
        
        String username = authentication.getName();
        PedidoDTO pedidoActualizado = pedidoService.actualizarEstado(id, idEstado, username);
        return ResponseEntity.ok(pedidoActualizado);
    }

    @PreAuthorize("hasRole('REPARTIDOR')")
    @GetMapping("/repartidor/mis-pedidos")
    public ResponseEntity<List<PedidoDTO>> obtenerPedidosPorRepartidor(Authentication authentication) throws ServiceException {
        String username = authentication.getName();
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorRepartidor(username));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/cliente/mis-pedidos")
    public ResponseEntity<List<PedidoDTO>> obtenerPedidosPorCliente(Authentication authentication) throws ServiceException {
        String username = authentication.getName();
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorCliente(username));
    }
}
