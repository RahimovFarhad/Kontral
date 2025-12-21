package com.example.Job_Post.service;


import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.Job_Post.auth.AuthenticationRequest;
import com.example.Job_Post.auth.AuthenticationResponse;
import com.example.Job_Post.auth.DeleteRequest;
import com.example.Job_Post.config.JwtService;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

import com.example.Job_Post.enumerator.AuthMethod;
import com.example.Job_Post.enumerator.TokenType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    private static final int verificationTokenByteLength = 32;
    private static final int verificationTokenExpiryMinute = 30;



    public void authenticate(AuthenticationRequest request, HttpServletResponse response) throws AccessDeniedException {
        request.setEmail(request.getEmail().toLowerCase());
        
        User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

        if (!user.getAuthMethod().equals(AuthMethod.Custom)) {
            throw new IllegalArgumentException("Try logging in with " + user.getAuthMethod() + " method");
        }

 
        authenticationManager.authenticate (
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        if (user.getVerified() == null || !user.getVerified()){
            final String verificationToken = verificationTokenService.generateToken(verificationTokenByteLength);
            user.setVerificationTokenHash(passwordEncoder.encode(verificationToken));
            user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(verificationTokenExpiryMinute));
            
            userRepository.save(user);

            emailService.sendVerificationEmail(user.getEmail(), verificationToken);

            throw new AccessDeniedException("This account is not verified!");
        }




      
        String refreshToken = JwtService.generateToken(user, TokenType.REFRESH);
        // String accessToken = JwtService.generateToken(user, TokenType.ACCESS);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true) // localhost
                    .sameSite("None") // required for cross-origin
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

        response.setHeader("Set-Cookie", cookie.toString());

        return;


        // return AuthenticationResponse.builder()
        //                              .token(accessToken)
        //                              .build(); 
    }

    public String deleteUser(String email, @RequestBody DeleteRequest request) {
        // Optional: re-authenticate with password if needed for extra safety
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                email,
                request.getPassword()
            )
        );

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        userRepository.delete(user);
        return "User deleted successfully";
    }



    
}


