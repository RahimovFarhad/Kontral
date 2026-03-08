package com.example.Job_Post.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.Job_Post.config.JwtService;
import com.example.Job_Post.entity.RefreshToken;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.TokenType;
import com.example.Job_Post.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String createRefreshToken(User user) {
        RefreshToken savedToken = refreshTokenRepository.save(
            RefreshToken.builder()
                .user(user)
                .expiryTime(Instant.now().plus(REFRESH_TOKEN_TTL))
                .revoked(false)
                .build()
        );

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.REFRESH.name().toLowerCase());
        claims.put("refreshTokenId", savedToken.getId());

        return JwtService.generateToken(claims, user, TokenType.REFRESH);
    }

    @Transactional
    public User consumeRefreshToken(String refreshToken) {
        if (!JwtService.isTokenSignatureValid(refreshToken, TokenType.REFRESH)) {
            throw new IllegalArgumentException("Invalid signature");
        }

        if (JwtService.extractExpiration(refreshToken, TokenType.REFRESH).before(new Date())) {
            throw new IllegalArgumentException("Token expired");
        }

        if (!JwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Not a refresh token");
        }

        Integer refreshTokenId = JwtService.extractClaim(
            refreshToken,
            TokenType.REFRESH,
            claims -> claims.get("refreshTokenId", Integer.class)
        );

        if (refreshTokenId == null) {
            throw new IllegalArgumentException("Missing refresh token id");
        }

        RefreshToken storedToken = refreshTokenRepository.findByIdAndRevokedFalse(refreshTokenId)
            .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (storedToken.getExpiryTime() != null && storedToken.getExpiryTime().isBefore(Instant.now())) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new IllegalArgumentException("Refresh token expired");
        }

        String email = JwtService.extractUsername(refreshToken, TokenType.REFRESH);
        if (!storedToken.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Refresh token user mismatch");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return storedToken.getUser();
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        try {
            Integer refreshTokenId = JwtService.extractClaim(
                refreshToken,
                TokenType.REFRESH,
                claims -> claims.get("refreshTokenId", Integer.class)
            );

            if (refreshTokenId == null) {
                return;
            }

            refreshTokenRepository.findById(refreshTokenId).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        } catch (Exception ignored) {
        }
    }

    @Transactional
    public void revokeAllByUserEmail(String email) {
        refreshTokenRepository.deleteByUserEmail(email);
    }
}
