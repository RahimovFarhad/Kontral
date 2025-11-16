package com.example.Job_Post.service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private final SecureRandom secureRandom; // Thread-safe
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();


    public String generateToken(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);
        return base64UrlEncoder.encodeToString(randomBytes);
    }
}
