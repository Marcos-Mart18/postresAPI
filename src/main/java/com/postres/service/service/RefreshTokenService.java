package com.postres.service.service;

import com.postres.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByUsuarioId(Long userId);
    RefreshToken save(RefreshToken refreshToken);
}
