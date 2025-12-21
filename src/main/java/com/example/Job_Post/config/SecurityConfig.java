package com.example.Job_Post.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.Job_Post.auth.CustomAuthorizationRequestResolver;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final  AuthenticationProvider authenticationProvider;
    private final OAuth2Handler oAuth2Handler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/api/v1/user/login",
                    "/api/v1/user/register",
                    "/api/v1/user/authenticate",
                    "/api/v1/user/users",
                    "/api/v1/user/register/verify",
                    "/api/v1/user/forgot-password",
                    "/api/v1/user/reset-password",
                    "/api/v1/user/validate-reset-token",
                    "/api/v1/user/profile/*",
                    "/api/v1/auth/refresh",
                    "/index.html",
                    "/js/**",
                    "/app/**",
                    "/ws/**",
                    "/oauth2/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/posts/all").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/posts/{id}").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
                })
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(oauth -> oauth
                .authorizationEndpoint(endpoint -> endpoint
                    .baseUri("/oauth2/authorize") // e.g. /oauth2/authorize/google
                    .authorizationRequestResolver(
                        new CustomAuthorizationRequestResolver(
                            clientRegistrationRepository, "/oauth2/authorize"))
                )
                .successHandler((request, response, authentication) -> {
                    oAuth2Handler.generateJwtForOAuth2User(authentication, response);
                    // String redirectUrl = "http://localhost:5173/";
                    String redirectUrl = "https://kontral.onrender.com";

                    // Send JWT in JSON instead of redirect
                    // response.setContentType("application/json");
                    // response.setCharacterEncoding("UTF-8");
                    // response.getWriter().flush();
                    
                    // response.sendRedirect(redirectUrl);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("""
                    {
                    "redirectUrl": "%s"
                    }
                    """.formatted(redirectUrl));
                    response.getWriter().flush();

                })
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"OAuth2 authentication failed\"}");
                })
            
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://kontral.onrender.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/index.html",
            "/favicon.ico",
            "/css/**",
            "/js/**",
            "/img/**"
        );
    }





    
}
