package com.postres.service.notifications;

import com.postres.dto.PedidoDTO;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public NotificationService() {}

    public void notifyClienteCambioEstado(Long idUsuarioCliente, String evento, PedidoDTO pedido) {
        // no-op (WebSocket deshabilitado)
    }

    public void notifyRepartidorAsignacion(Long idRepartidor, PedidoDTO pedido) {
        // no-op (WebSocket deshabilitado)
    }

    public static class Notificacion {
        private String tipo;
        private PedidoDTO pedido;

        public Notificacion() {}
        public Notificacion(String tipo, PedidoDTO pedido) {
            this.tipo = tipo;
            this.pedido = pedido;
        }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public PedidoDTO getPedido() { return pedido; }
        public void setPedido(PedidoDTO pedido) { this.pedido = pedido; }
    }
}
