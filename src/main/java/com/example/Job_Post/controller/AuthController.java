package com.example.Job_Post.controller;

import java.util.Arrays;
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
            String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);


            String username = JwtService.extractUsername(refreshToken); // Extract username from the JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Load user details from the database

            

            if (refreshToken == null || !JwtService.isTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            User user = userService.getUserByEmail(username);
            String newAccessToken = JwtService.generateToken(user, TokenType.ACCESS);

            // optional: rotate refresh token
            String newRefreshToken = JwtService.generateToken(user, TokenType.REFRESH);
            Cookie cookie = new Cookie("refreshToken", newRefreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/api/v1/auth");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of("token", newAccessToken));
        }
        catch (Exception e) {

            return ResponseEntity.badRequest().body("Cannot refresh!");

            
        }
    }

    
}
