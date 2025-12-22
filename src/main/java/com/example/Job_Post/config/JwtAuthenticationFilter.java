package com.example.Job_Post.config;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Job_Post.entity.User;
import com.example.Job_Post.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Service to handle JWT operations
    private final UserDetailsService userDetailsService;

    private final UserService userService;

    @SuppressWarnings("static-access")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = null;
        try {
            path = request.getServletPath();
            System.out.println(path);
            if (path.equals("/api/v1/user/register") || 
                path.equals("/api/v1/user/authenticate") || 
                path.equals("/api/v1/user/users") || 
                path.equals("/api/v1/user/register/verify") ||
                path.equals("/api/v1/auth/refresh") ||
                path.startsWith("/api/v1/user/profile/") ||
                path.equals("/api/v1/user/forgot-password") ||
                path.equals("/api/v1/user/reset-password") ||
                path.equals("/api/v1/user/validate-reset-token") ||
                path.startsWith("/js/") || 
                path.startsWith("/css/") || 
                path.startsWith("/images/") || 
                path.startsWith("/ws/") ||
                path.startsWith("/oauth2") ||
                path.equals("/") ||
                path.equals("/index.html") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/assets/") ||
                path.equals("/vite.svg")
                !path.startsWith("/api/")
                ) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization"); 
            // System.out.println("🔍 Incoming Authorization Header: " + authHeader);
            // System.out.println("🔍 Request Path: " + path);

        
            

            final String jwt;
            final String username;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response); // Continue the filter chain if no JWT is present
                return;
            }


            jwt = authHeader.substring(7); // Extract the JWT from the header 
            username = JwtService.extractUsername(jwt); // Extract username from the JWT

            if (username != null  && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // Load user details from the database

                User user = userService.getUserByEmail(username);
                if (!user.getVerified()){
                    throw new AccessDeniedException("This account is not verified!");
                }

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request)); // Set the details of the authentication token
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken); // Set the authentication in the security context
                    
                }

                System.out.println("Authenticated");

            }


            filterChain.doFilter(request, response); // Continue the filter chain



        }
        catch (Exception e) {
            if (
                path != null && (
                (request.getMethod().equals("GET") && path.equals("/api/v1/posts/all")) ||
                (request.getMethod().equals("GET") && path.startsWith("/api/v1/posts/"))
                )
            ){
                System.out.println("Public access to posts endpoint: " + path);
                filterChain.doFilter(request, response); // Continue the filter chain
                return;
            }
                
            System.out.println("Error during authentication: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
            // filterChain.doFilter(request, response);
            return;
        }
    
    }
}
