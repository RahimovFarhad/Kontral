package com.example.Job_Post.config;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.Job_Post.auth.AuthenticationResponse;
import com.example.Job_Post.auth.RegisterRequest;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.UserRepository;
import com.example.Job_Post.service.RefreshTokenService;
import com.example.Job_Post.service.RegisterService;

import jakarta.servlet.http.HttpServletResponse;

import com.example.Job_Post.enumerator.AuthMethod;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2Handler {
    private final RegisterService registerService;

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final CookieFactory cookieFactory;


    public void generateJwtForOAuth2User(Authentication authentication, HttpServletResponse response) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        // Extract user details (depends on provider, usually from attributes)
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        Boolean emailVerified = (Boolean) attributes.getOrDefault("email_verified", true);
        if (!emailVerified) {
            throw new SecurityException("Email is not verified by provider");
        }

        String email = (String) attributes.get("email");  // usually email is here
        // String name = (String) attributes.get("name");    // maybe name too

        if (userRepository.existsByEmail(email)) {
            // User already exists, generate token
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
            String refreshToken = refreshTokenService.createRefreshToken(user);

            ResponseCookie cookie = cookieFactory.refreshToken(refreshToken);

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            

            

            return;
        }
        
        try {
            registerService.registerOAuth(new RegisterRequest(null, null, email, null, null), AuthMethod.OAuth2, response);
        } catch (Exception e) {
            // Handle registration failure, maybe log it or throw a custom exception
            System.err.println("Error during OAuth2 registration: " + e.getMessage());
            return; // or throw an exception
        }


        return;
    }

    public static String getJwtFromResponse(AuthenticationResponse response) {
        if (response == null || response.getToken() == null) {
            throw new IllegalArgumentException("AuthenticationResponse or token cannot be null");
        }
        return response.getToken();
    }
}
