package com.postres.service.notifications;

import com.postres.dto.PedidoDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Notifica al cliente cuando cambia el estado de su pedido
     */
    public void notifyClienteCambioEstado(Long idUsuarioCliente, String evento, PedidoDTO pedido) {
        Notificacion notificacion = new Notificacion(evento, pedido);
        // WebSocket
        messagingTemplate.convertAndSend("/topic/cliente/" + idUsuarioCliente, notificacion);
        // HTTP polling fallback
        com.postres.controller.general.NotificacionController.agregarNotificacionCliente(idUsuarioCliente, notificacion);
    }

    /**
     * Notifica al repartidor cuando se le asigna un pedido
     */
    public void notifyRepartidorAsignacion(Long idRepartidor, PedidoDTO pedido) {
        Notificacion notificacion = new Notificacion("PEDIDO_ASIGNADO", pedido);
        // WebSocket
        messagingTemplate.convertAndSend("/topic/repartidor/" + idRepartidor, notificacion);
        // HTTP polling fallback
        com.postres.controller.general.NotificacionController.agregarNotificacionRepartidor(idRepartidor, notificacion);
    }

    /**
     * Notifica al cliente cuando el repartidor inicia la entrega
     */
    public void notifyClienteEntregaIniciada(Long idUsuarioCliente, PedidoDTO pedido) {
        Notificacion notificacion = new Notificacion("ENTREGA_INICIADA", pedido);
        // WebSocket
        messagingTemplate.convertAndSend("/topic/cliente/" + idUsuarioCliente, notificacion);
        // HTTP polling fallback
        com.postres.controller.general.NotificacionController.agregarNotificacionCliente(idUsuarioCliente, notificacion);
    }

    /**
     * Notifica al cliente cuando el pedido fue entregado
     */
    public void notifyClienteEntregado(Long idUsuarioCliente, PedidoDTO pedido) {
        Notificacion notificacion = new Notificacion("PEDIDO_ENTREGADO", pedido);
        // WebSocket
        messagingTemplate.convertAndSend("/topic/cliente/" + idUsuarioCliente, notificacion);
        // HTTP polling fallback
        com.postres.controller.general.NotificacionController.agregarNotificacionCliente(idUsuarioCliente, notificacion);
    }

    public static class Notificacion {
        private String tipo;
        private PedidoDTO pedido;
        private String mensaje;
        private long timestamp;

        public Notificacion() {}
        
        public Notificacion(String tipo, PedidoDTO pedido) {
            this.tipo = tipo;
            this.pedido = pedido;
            this.timestamp = System.currentTimeMillis();
            this.mensaje = generarMensaje(tipo, pedido);
        }

        private String generarMensaje(String tipo, PedidoDTO pedido) {
            String numOrden = pedido != null && pedido.getNumOrden() != null ? pedido.getNumOrden() : "N/A";
            switch (tipo) {
                case "PEDIDO_ACEPTADO": return "Tu pedido " + numOrden + " ha sido aceptado";
                case "EN_PREPARACION": return "Tu pedido " + numOrden + " está en preparación";
                case "LISTO_PARA_ENTREGA": return "Tu pedido " + numOrden + " está listo para entrega";
                case "PEDIDO_ASIGNADO": return "Se te ha asignado el pedido " + numOrden;
                case "ENTREGA_INICIADA": return "Tu pedido " + numOrden + " está en camino";
                case "PEDIDO_ENTREGADO": return "Tu pedido " + numOrden + " ha sido entregado";
                case "PEDIDO_CANCELADO": return "Tu pedido " + numOrden + " ha sido cancelado";
                default: return "Actualización del pedido " + numOrden;
            }
        }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public PedidoDTO getPedido() { return pedido; }
        public void setPedido(PedidoDTO pedido) { this.pedido = pedido; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
