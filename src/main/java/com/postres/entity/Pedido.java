package com.postres.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "PEDIDO")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PEDIDO")
    @SequenceGenerator(name = "SQ_PEDIDO", sequenceName = "SQ_PEDIDO", allocationSize = 1)
    @Column(name = "id_pedido",columnDefinition = "NUMBER")
    private Long idPedido;

    @Column(name = "fecha_entrega", columnDefinition = "DATE")
    private LocalDate fechaEntrega;

    @Column(name = "hora_entrega", columnDefinition = "VARCHAR2(5)")
    private String horaEntrega;

    @CreatedDate
    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private LocalDate fechaPedido;

    @Column(name = "total",columnDefinition = "NUMBER(5,2)")
    private Double total;

    @Column(name = "num_orden", unique = true)
    private String numOrden;

    @Column(name = "direccion",columnDefinition = "varchar2(200)")
    private String direccion;


    @PrePersist
    protected void onCreate() {
        this.fechaPedido = LocalDate.now();
    }


    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;


    @ManyToOne
    @JoinColumn(name = "id_repartidor")
    private Repartidor repartidor;


    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;


    @OneToMany(mappedBy = "pedido")
    @JsonIgnore
    private List<DetallePedido> detallePedidos;


}
