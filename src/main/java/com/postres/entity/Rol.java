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
@Table(name = "ROL")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_ROL")
    @SequenceGenerator(name = "SQ_ROL", sequenceName = "SQ_ROL", allocationSize = 1)
    @Column(name = "id_rol",columnDefinition = "NUMBER")
    private Long idRol;
    @Column(name = "nombre",columnDefinition = "varchar(100)")
    private String nombre;
    @Column(name = "is_active",columnDefinition = "char(1)")
    private char isActive='A';


    @OneToMany(mappedBy = "rol")
    @JsonIgnore
    private List<UsuarioRol> usuarioRoles;
}
