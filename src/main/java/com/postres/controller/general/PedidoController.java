package com.postres.controller.general;

import com.postres.dto.PedidoDTO;
import com.postres.service.service.PedidoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {

        this.pedidoService = pedidoService;
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/create")
    public ResponseEntity<PedidoDTO> createByUser(@RequestBody PedidoDTO pedidoDTO, Authentication authentication) {
        try {
            String username = authentication.getName();

            PedidoDTO pedidoCreado = pedidoService.createByUser(pedidoDTO, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCreado);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
