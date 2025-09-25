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
@Table(name = "CATEGORIA")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_CATEGORIA")
    @SequenceGenerator(name = "SQ_CATEGORIA", sequenceName = "SQ_CATEGORIA", allocationSize = 1)
    @Column(name = "id_categoria",columnDefinition = "NUMBER")
    private Long idCategoria;
    @Column(name = "nombre",columnDefinition = "varchar(100)")
    private String nombre;
    @Column(name = "is_active",columnDefinition = "char(1)")
    private char isActive='A';


    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Producto> productos;
}
