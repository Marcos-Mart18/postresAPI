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
@Table(name = "PRODUCTO")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PRODUCTO")
    @SequenceGenerator(name = "SQ_PRODUCTO", sequenceName = "SQ_PRODUCTO", allocationSize = 1)
    @Column(name = "id_producto",columnDefinition = "NUMBER")
    private Long idProducto;
    @Column(name = "nombre",columnDefinition = "varchar2(150)")
    private String nombre;
    @Column(name = "foto_url",columnDefinition = "varchar2(200)")
    private String fotoUrl;
    @Column(name = "precio",columnDefinition = "NUMBER(5,2)")
    private Double precio;
    @Column(name = "descripcion",columnDefinition = "varchar2(600)")
    private String descripcion;
    @Column(name = "is_active",columnDefinition = "char(1)")
    private char isActive='A';


    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @OneToMany(mappedBy = "producto")
    @JsonIgnore
    private List<DetallePedido> detallePedidos;
}
