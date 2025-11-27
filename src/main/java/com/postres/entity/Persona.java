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
@Table(name = "PERSONA")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_PERSONA")
    @SequenceGenerator(name = "SQ_PERSONA", sequenceName = "SQ_PERSONA", allocationSize = 1)
    @Column(name = "id_persona",columnDefinition = "NUMBER")
    private Long idPersona;
    @Column(name = "nombres",columnDefinition = "varchar2(150)")
    private String nombres;
    @Column(name = "apellidos",columnDefinition = "varchar2(150)")
    private String apellidos;
    @Column(name = "dni",columnDefinition = "char(8)")
    private String dni;
    @Column(name = "correo",columnDefinition = "varchar2(320)")
    private String correo;
    @Column(name = "telefono",columnDefinition = "varchar2(15)")
    private String telefono;
    @Column(name = "direccion",columnDefinition = "varchar2(200)")
    private String direccion;
    @Column(name = "is_active",columnDefinition = "char(1)")
    private char isActive='A';

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Usuario> usuarios;
}
