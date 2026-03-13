package com.sabin.config;

import com.sabin.filter.JwtAuthenticationFilter;
import com.sabin.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Marks this class as a configuration class for Spring
@EnableMethodSecurity // Enables annotations like @PreAuthorize
public class SecurityConfig {

    // JWT filter that validates token in each request
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Custom service that loads user details from database
    @Autowired
    private CustomUserDetailsService userDetailsService;

    // Password encoder bean (used to encrypt passwords)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Provides AuthenticationManager used for login authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Main Spring Security configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

            // Disable CSRF since we are using JWT (stateless API)
            .csrf(csrf -> csrf.disable())

            // This project uses JWT, so we do not need Spring's default login page.
            .formLogin(form -> form.disable())

            // This keeps API auth focused on JWT instead of browser/basic-auth flows.
            .httpBasic(httpBasic -> httpBasic.disable())

            // Make application stateless (no session stored on server)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Define authorization rules
            .authorizeHttpRequests(auth -> auth

                    // These endpoints must stay public so Postman can register/login first.
                    .requestMatchers("/api/auth/register", "/api/auth/login", "/error").permitAll()

                    // All other endpoints require authentication
                    .anyRequest().authenticated()
            )

            // Return 401 for protected endpoints when the user is not logged in.
            .exceptionHandling(ex -> ex.authenticationEntryPoint(
                    (request, response, authException) ->
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
            ))

            // Read the JWT token before Spring checks protected endpoints.
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
