package com.example.Job_Post.controller;

import java.security.Principal;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.auth.DeleteRequest;
import com.example.Job_Post.service.AuthenticationService;
import com.example.Job_Post.service.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/terminate")
@RequiredArgsConstructor
public class DeleteController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody DeleteRequest request, Principal principal) {
        try {
            String email = principal.getName(); // Extracted from JWT token
            return ResponseEntity.ok(authenticationService.deleteUser(email, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete user: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Arrays.stream(cookies)
                    .filter(c -> "refreshToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .ifPresent(refreshTokenService::revokeRefreshToken);
            }

            ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
                    .httpOnly(true)
                    .secure(true) 
                    .sameSite("None")
                    .path("/")
                    .maxAge(0)
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                
            ResponseEntity<String> res = ResponseEntity.ok("Logged out successfully!");

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Logout failed: " + e.getMessage());
        }                         
    }
}
