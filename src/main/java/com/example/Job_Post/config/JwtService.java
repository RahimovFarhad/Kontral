package com.example.Job_Post.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.Job_Post.enumerator.TokenType;
import org.springframework.stereotype.Service;
import com.example.Job_Post.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    private static SecretKey ACCESS_SIGNING_KEY;
    private static SecretKey REFRESH_SIGNING_KEY;

    @Value("${SECRET_KEY}")
    private String accessSecret;

    @Value("${REFRESH_SECRET_KEY}")
    private String refreshSecret;

    @PostConstruct
    public void init() {
        if (accessSecret == null || accessSecret.isBlank()) {
            throw new IllegalStateException("SECRET_KEY env var is missing");
        }

        if (refreshSecret == null || refreshSecret.isBlank()) {
            throw new IllegalStateException("REFRESH_SECRET_KEY env var is missing");
        }

        byte[] accessKeyBytes = Decoders.BASE64.decode(accessSecret);
        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecret);

        ACCESS_SIGNING_KEY = Keys.hmacShaKeyFor(accessKeyBytes);
        REFRESH_SIGNING_KEY = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public static String extractUsername(String token) {
        return extractUsername(token, TokenType.ACCESS);
    }

    public static String extractUsername(String token, TokenType tokenType) {
        return extractClaim(token, tokenType, Claims::getSubject);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return extractClaim(token, TokenType.ACCESS, claimsResolver);
    }

    public static <T> T extractClaim(String token, TokenType tokenType, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token, TokenType tokenType) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("❌ JWT is null or empty before parsing.");
        }

        return Jwts
            .parser()
            .verifyWith(getSignInKey(tokenType))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private static SecretKey getSignInKey(TokenType tokenType) {
        return tokenType == TokenType.REFRESH ? REFRESH_SIGNING_KEY : ACCESS_SIGNING_KEY;
    }

    public static boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static Date extractExpiration(String token) {
        return extractExpiration(token, TokenType.ACCESS);
    }

    public static Date extractExpiration(String token, TokenType tokenType) {
        return extractClaim(token, tokenType, Claims::getExpiration);
    }

    public static String generateToken(User user, TokenType tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", tokenType.name().toLowerCase());
        return generateToken(claims, user, tokenType);
    }

    public static String generateToken(Map<String, Object> extraClaims, User user, TokenType tokenType) {
        int time = (tokenType.equals(TokenType.REFRESH))
                ? 1000 * 60 * 60 * 24 * 7
                // : 1000 * 60 * 10;
                : 1000 * 10;

        return Jwts
            .builder()
            .claims(extraClaims)
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + time))
            .signWith(getSignInKey(tokenType))
            .compact();
    }

    public static String generateTokenByEmail(String email, TokenType tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", tokenType.name().toLowerCase());

        int time = (tokenType.equals(TokenType.REFRESH))
                ? 1000 * 60 * 60 * 24 * 7
                : 1000 * 60 * 10;

        return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + time))
            .signWith(getSignInKey(tokenType))
            .compact();
    }

    public static boolean isRefreshToken(String token) {
        try {
            String type = extractClaim(token, TokenType.REFRESH, c -> c.get("type", String.class));
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isTokenSignatureValid(String token) {
        return isTokenSignatureValid(token, TokenType.ACCESS);
    }

    public static boolean isTokenSignatureValid(String token, TokenType tokenType) {
        try {
            extractAllClaims(token, tokenType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRefreshTokenValid(String token) {
        return isTokenSignatureValid(token, TokenType.REFRESH)
            && isRefreshToken(token)
            && !extractExpiration(token, TokenType.REFRESH).before(new Date());
    }

}
