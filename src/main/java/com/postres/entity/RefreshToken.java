package com.postres.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "REFRESH_TOKEN")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_REFRESH_TOKEN")
    @SequenceGenerator(name = "SQ_REFRESH_TOKEN", sequenceName = "SQ_REFRESH_TOKEN", allocationSize = 1)
    @Column(name = "id_refresh_token", columnDefinition = "NUMBER")
    private Long idRefreshToken;

    @Column(name = "refresh_token", columnDefinition = "varchar2(500)", nullable = false)
    private String refreshToken;

    @Column(name = "expiry_date", columnDefinition = "DATE", nullable = false)
    private Date expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = true)
    private Usuario usuario;

}
