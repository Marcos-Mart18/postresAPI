package com.postres.service.Impl;

import com.postres.entity.RefreshToken;
import com.postres.repository.RefreshTokenRepository;
import com.postres.service.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByRefreshToken(token);
    }

    @Override
    public void deleteByUsuarioId(Long userId) {
        refreshTokenRepository.deleteByUsuario_IdUsuario(userId);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }
}
