package com.example.Job_Post.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieFactory {

    @Value("${app.cookie.secure}")
    private boolean secure;

    @Value("${app.cookie.same-site}")
    private String sameSite;

    private static final String REFRESH_COOKIE = "refreshToken";
    private static final long REFRESH_MAX_AGE = 7 * 24 * 60 * 60;

    /** Build the refresh-token cookie using the active profile's security policy. */
    public ResponseCookie refreshToken(String token) {
        return build(token, REFRESH_MAX_AGE);
    }

    /** Build an expired refresh-token cookie (for logout). */
    public ResponseCookie expiredRefreshToken() {
        return build(null, 0);
    }

    private ResponseCookie build(String value, long maxAge) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
