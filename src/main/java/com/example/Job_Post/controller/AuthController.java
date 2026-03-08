package com.example.Job_Post.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.config.JwtService;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.TokenType;
import com.example.Job_Post.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenService refreshTokenService;

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

            User user = refreshTokenService.consumeRefreshToken(refreshToken);
            String newAccess = JwtService.generateTokenByEmail(user.getEmail(), TokenType.ACCESS);
            String newRefresh = refreshTokenService.createRefreshToken(user);

            ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefresh)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());


            return ResponseEntity.ok(Map.of("token", newAccess));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Cannot refresh: " + e.getMessage());
        }
    }


}
