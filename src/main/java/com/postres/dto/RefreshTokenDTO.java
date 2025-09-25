package com.postres.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshTokenDTO {
    private Long idRefreshToken;
    private String refreshToken;
    private Date expiryDate;
    private Long idUsuario;
}
