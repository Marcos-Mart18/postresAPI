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
@Table(name = "USUARIO")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_USUARIO")
    @SequenceGenerator(name = "SQ_USUARIO", sequenceName = "SQ_USUARIO", allocationSize = 1)
    @Column(name = "id_usuario",columnDefinition = "NUMBER")
    private Long idUsuario;
    @Column(name = "username",columnDefinition = "varchar2(100)")
    private String username;
    @Column(name = "contrasena",columnDefinition = "varchar2(200)")
    private String contrasena;
    @Column(name = "nombres",columnDefinition = "varchar2(150)")
    private String nombres;
    @Column(name = "apellidos",columnDefinition = "varchar2(150)")
    private String apellidos;
    @Column(name = "dni",columnDefinition = "char(8)")
    private String dni;
    @Column(name = "foto_url",columnDefinition = "varchar2(200)")
    private String fotoUrl;
    @Column(name = "correo",columnDefinition = "varchar2(320)")
    private String correo;
    @Column(name = "is_active",columnDefinition = "char(1)")
    private char isActive='A';

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<UsuarioRol> usuarioRoles;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RefreshToken> refreshTokens;

    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = true)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_repartidor", nullable = true)
    private Repartidor repartidor;
}
