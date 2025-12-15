package com.example.Job_Post.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.config.JwtService;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.TokenType;
import com.example.Job_Post.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final UserService userService;


    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {
        // If the request reaches here, token is valid
        return ResponseEntity.ok("Token is valid");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return ResponseEntity.status(401).body("No cookies present");
            }

            String refreshToken = Arrays.stream(cookies)
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

            if (refreshToken == null) {
                return ResponseEntity.status(401).body("Missing refresh token");
            }

            if (!JwtService.isTokenSignatureValid(refreshToken)) {
                return ResponseEntity.status(401).body("Invalid signature");
            }

            if (JwtService.extractExpiration(refreshToken).before(new Date())) {
                return ResponseEntity.status(401).body("Token expired");
            }

            if (!JwtService.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(401).body("Not a refresh token");
            }

            String email = JwtService.extractUsername(refreshToken);

            // No DB lookup needed!

            String newAccess = JwtService.generateTokenByEmail(email, TokenType.ACCESS);
            String newRefresh = JwtService.generateTokenByEmail(email, TokenType.REFRESH);

            Cookie cookie = new Cookie("refreshToken", newRefresh);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/api/v1/auth");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.setHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok(Map.of("token", newAccess));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Cannot refresh");
        }
    }

}