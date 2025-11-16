package com.example.Job_Post.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import com.example.Job_Post.enumerator.TokenType;


import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


private static Claims extractAllClaims(String token) {
    // System.out.println("🪪 JWT before parsing: " + token);
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
        byte[] keyBytes = Decoders.BASE64.decode(System.getenv("SECRET_KEY"));
        return Keys.hmacShaKeyFor(keyBytes); 
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
        return generateToken(new HashMap<>(), user, tokenType);
    }

    public static String generateToken(Map<String, Object> extraClaims, User user, TokenType tokenType) {
        int time = (tokenType.equals(TokenType.REFRESH)) ? 1000 * 60 * 60 * 24 * 7 : 1000 * 60 * 10;

        return Jwts
            .builder()
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new  Date(System.currentTimeMillis() + time)) // 10 hours
            .signWith(getSignInKey())
            .claims(extraClaims)
            .compact();
    }
    public static String generateTokenByEmail (String email, TokenType tokenType) {
        int time = (tokenType.equals(TokenType.REFRESH)) ? 1000 * 60 * 60 * 24 * 7 : 1000 * 60 * 10;

        return Jwts
            .builder()
            .subject(email)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + time)) // 10 hours
            .signWith(getSignInKey())
            .compact();
    }


    

    


}