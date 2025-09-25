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
@Table(name = "REPARTIDOR")
public class Repartidor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_REPARTIDOR")
    @SequenceGenerator(name = "SQ_REPARTIDOR", sequenceName = "SQ_REPARTIDOR", allocationSize = 1)
    @Column(name = "id_repartidor",columnDefinition = "NUMBER")
    private Long idRepartidor;
    @Column(name = "codigo",columnDefinition = "char(4)")
    private String codigo;
    @Column(name = "is_active",columnDefinition = "char(1)")
    private char isActive='A';


    @OneToMany(mappedBy = "repartidor")
    @JsonIgnore
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "repartidor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Usuario> usuarios;
}
