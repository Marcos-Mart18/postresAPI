package com.postres.repository;

import com.postres.entity.Pedido;
import com.postres.entity.Repartidor;
import com.postres.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByNumOrden(String numOrden);
    
    List<Pedido> findByRepartidorIdRepartidor(Long idRepartidor);
    
    List<Pedido> findByUsuarioIdUsuario(Long idUsuario);
    
    List<Pedido> findByRepartidor(Repartidor repartidor);
    
    List<Pedido> findByUsuario(Usuario usuario);
}
