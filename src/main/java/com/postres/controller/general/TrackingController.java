package com.postres.controller.general;

import com.postres.dto.UbicacionDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RestController
@RequestMapping("/api/v1/tracking")
public class TrackingController {

    private final SimpMessagingTemplate messagingTemplate;
    
    // Cache en memoria de ubicaciones actuales (en producción usar Redis)
    private final Map<Long, UbicacionDTO> ubicacionesActuales = new ConcurrentHashMap<>();

    public TrackingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Endpoint WebSocket: El repartidor envía su ubicación
     * El cliente escucha en /topic/tracking/{pedidoId}
     */
    @MessageMapping("/ubicacion")
    public void actualizarUbicacion(UbicacionDTO ubicacion) {
        ubicacion.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Guardar en cache
        ubicacionesActuales.put(ubicacion.getPedidoId(), ubicacion);
        
        // Enviar a todos los suscriptores del pedido específico
        messagingTemplate.convertAndSend(
            "/topic/tracking/" + ubicacion.getPedidoId(), 
            ubicacion
        );
    }

    /**
     * Endpoint REST: Obtener última ubicación conocida del repartidor para un pedido
     */
    @GetMapping("/pedido/{pedidoId}")
    public UbicacionDTO getUbicacionPedido(@PathVariable Long pedidoId) {
        return ubicacionesActuales.getOrDefault(pedidoId, null);
    }

    /**
     * Endpoint REST: El repartidor actualiza su ubicación (alternativa a WebSocket)
     */
    @PostMapping("/actualizar")
    public UbicacionDTO actualizarUbicacionRest(@RequestBody UbicacionDTO ubicacion) {
        ubicacion.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        ubicacionesActuales.put(ubicacion.getPedidoId(), ubicacion);
        
        // También notificar por WebSocket
        messagingTemplate.convertAndSend(
            "/topic/tracking/" + ubicacion.getPedidoId(), 
            ubicacion
        );
        
        return ubicacion;
    }
}
