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
@Table(name = "USUARIO_ROL")
public class UsuarioRol {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_USUARIO_ROL")
    @SequenceGenerator(name = "SQ_USUARIO_ROL", sequenceName = "SQ_USUARIO_ROL", allocationSize = 1)
    @Column(name = "idusuario_rol",columnDefinition = "NUMBER")
    private Long idUsuarioRol;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
}
