package com.postres.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "DETALLE_PEDIDO")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_DETALLE_PEDIDO")
    @SequenceGenerator(name = "SQ_DETALLE_PEDIDO", sequenceName = "SQ_DETALLE_PEDIDO", allocationSize = 1)
    @Column(name = "id_detalle_pedido",columnDefinition = "NUMBER")
    private Long idDetallePedido;
    @Column(name = "cantidad",columnDefinition = "NUMBER")
    private Long cantidad;
    @Column(name = "is_active",columnDefinition = "char(1)")
    private char isActive='A';


    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
}
