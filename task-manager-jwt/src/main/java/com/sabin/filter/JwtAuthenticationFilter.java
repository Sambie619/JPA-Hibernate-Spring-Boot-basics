package com.sabin.filter;

import com.sabin.service.CustomUserDetailsService;
import com.sabin.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Registers this filter as a Spring Bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Utility class used for extracting and validating JWT tokens
    @Autowired
    private JwtUtil jwtUtil;

    // Service used to load user details from the database
    @Autowired
    private CustomUserDetailsService userDetailsService;

    // This method runs for EVERY incoming HTTP request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        // Get the Authorization header from the request
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check if header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            // Extract the token part after "Bearer "
            jwt = authorizationHeader.substring(7);

            try {
                // Extract username only if the token format and signature are valid.
                username = jwtUtil.extractUsername(jwt);
            } catch (JwtException | IllegalArgumentException exception) {
                // Bad tokens should act like "not logged in", not crash the whole request.
                filterChain.doFilter(request, response);
                return;
            }
        }

        // If username exists and user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validate the token (username + expiration)
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Create authentication token object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Attach request details (IP, session etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext
                // This tells Spring Security the user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue the request filter chain
        filterChain.doFilter(request, response);
    }
}

