package com.postres.repository;

import com.postres.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    @Query("select coalesce(sum(d.cantidad),0) from DetallePedido d where d.pedido.fechaEntrega = :fecha and upper(d.pedido.estado.nombre) <> 'CANCELADO'")
    Long sumCantidadByFechaEntregaExcluyendoCancelado(@Param("fecha") java.time.LocalDate fecha);
}
