package com.example.Job_Post.controller;

import java.security.Principal;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.auth.DeleteRequest;
import com.example.Job_Post.service.AuthenticationService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/terminate")
@RequiredArgsConstructor
public class DeleteController {

    private final AuthenticationService authenticationService;


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
    public ResponseEntity<String> logout(HttpServletResponse response) {
        try {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
                    .httpOnly(true)
                    .secure(false) // localhost
                    .sameSite("None") // required for cross-origin
                    .path("/api/v1/auth/refresh")
                    .maxAge(0)
                    .build();

            response.setHeader("Set-Cookie", cookie.toString());

                
            ResponseEntity<String> res = ResponseEntity.ok("Logged out successfully!");

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Logout failed: " + e.getMessage());
        }                         
    }
}
