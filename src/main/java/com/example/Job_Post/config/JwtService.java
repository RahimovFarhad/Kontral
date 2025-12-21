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

    private static SecretKey SIGNING_KEY;

    @Value("${SECRET_KEY}")
    private String secret;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("SECRET_KEY env var is missing");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        SIGNING_KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("❌ JWT is null or empty before parsing.");
        }

        return Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private static SecretKey getSignInKey() {
        return SIGNING_KEY;
    }

    public static boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static String generateToken(User user, TokenType tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", tokenType.name().toLowerCase());
        return generateToken(claims, user, tokenType);
    }

    public static String generateToken(Map<String, Object> extraClaims, User user, TokenType tokenType) {
        int time = (tokenType.equals(TokenType.REFRESH))
                ? 1000 * 60 * 60 * 24 * 7
                : 1000 * 60 * 10;

        return Jwts
            .builder()
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + time))
            .signWith(getSignInKey())
            .claims(extraClaims)
            .compact();
    }

    public static String generateTokenByEmail(String email, TokenType tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", tokenType.name().toLowerCase());

        int time = (tokenType.equals(TokenType.REFRESH))
                ? 1000 * 60 * 60 * 24 * 7
                : 1000 * 60 * 10;

        return Jwts.builder()
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + time))
            .claims(claims)
            .signWith(getSignInKey())
            .compact();
    }

    public static boolean isRefreshToken(String token) {
        try {
            String type = extractClaim(token, c -> c.get("type", String.class));
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isTokenSignatureValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRefreshTokenValid(String token) {
        return isTokenSignatureValid(token)
            && isRefreshToken(token)
            && !isTokenExpired(token);
    }

}
