package com.postres.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "ESTADO")
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_ESTADO")
    @SequenceGenerator(name = "SQ_ESTADO", sequenceName = "SQ_ESTADO", allocationSize = 1)
    @Column(name = "id_estado",columnDefinition = "NUMBER")
    private Long idEstado;
    @Column(name = "nombre",columnDefinition = "varchar(100)")
    private String nombre;


    @OneToMany(mappedBy = "estado")
    @JsonIgnore
    private List<Pedido> pedidos;
}
