package com.sabin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Marks this class as a configuration class for Spring Security
public class SecurityConfig {

    // Bean to encode passwords using BCrypt hashing algorithm
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean to define users stored in memory (not in database)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        // Creating a normal user with USER role
        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("user123")) // password is encoded
                .roles("USER")
                .build();

        // Creating an admin user with ADMIN role
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123")) // password is encoded
                .roles("ADMIN")
                .build();

        // Stores the users in memory
        return new InMemoryUserDetailsManager(user, admin);
    }

    // Configures security rules for HTTP requests
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable CSRF protection (often done for testing APIs)
        http.csrf(csrf -> csrf.disable());

        // Define authorization rules for different URL paths
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/public/**").permitAll() // accessible to everyone
                .requestMatchers("/user/**").hasRole("USER")    // only USER role
                .requestMatchers("/admin/**").hasRole("ADMIN")  // only ADMIN role
                .anyRequest().authenticated()                   // all other requests require login
        );

        // Configure custom login page
        http.formLogin(form -> form
                .loginPage("/login") // custom login URL
                .permitAll()        // everyone can access login page
        );

        // Enable logout functionality
        http.logout(logout -> logout.permitAll());

        // Build and return the security configuration
        return http.build();
    }
}

