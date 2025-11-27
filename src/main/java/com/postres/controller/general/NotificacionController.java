package com.postres.controller.general;

import com.postres.service.notifications.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    // Cache de notificaciones pendientes por usuario (en producción usar Redis)
    private static final Map<Long, List<NotificationService.Notificacion>> notificacionesCliente = new ConcurrentHashMap<>();
    private static final Map<Long, List<NotificationService.Notificacion>> notificacionesRepartidor = new ConcurrentHashMap<>();

    /**
     * Agregar notificación para un cliente (llamado internamente)
     */
    public static void agregarNotificacionCliente(Long idUsuario, NotificationService.Notificacion notificacion) {
        notificacionesCliente.computeIfAbsent(idUsuario, k -> new ArrayList<>()).add(notificacion);
    }

    /**
     * Agregar notificación para un repartidor (llamado internamente)
     */
    public static void agregarNotificacionRepartidor(Long idRepartidor, NotificationService.Notificacion notificacion) {
        notificacionesRepartidor.computeIfAbsent(idRepartidor, k -> new ArrayList<>()).add(notificacion);
    }

    /**
     * Obtener y limpiar notificaciones pendientes del cliente
     */
    @GetMapping("/cliente/{idUsuario}")
    public List<NotificationService.Notificacion> getNotificacionesCliente(@PathVariable Long idUsuario) {
        List<NotificationService.Notificacion> notificaciones = notificacionesCliente.remove(idUsuario);
        return notificaciones != null ? notificaciones : new ArrayList<>();
    }

    /**
     * Obtener y limpiar notificaciones pendientes del repartidor
     */
    @GetMapping("/repartidor/{idRepartidor}")
    public List<NotificationService.Notificacion> getNotificacionesRepartidor(@PathVariable Long idRepartidor) {
        List<NotificationService.Notificacion> notificaciones = notificacionesRepartidor.remove(idRepartidor);
        return notificaciones != null ? notificaciones : new ArrayList<>();
    }
}
