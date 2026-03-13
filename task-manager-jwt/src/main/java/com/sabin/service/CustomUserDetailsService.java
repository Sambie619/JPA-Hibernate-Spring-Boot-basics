package com.sabin.service;

import com.sabin.entity.User;
import com.sabin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service // Marks this class as a Spring Service component
public class CustomUserDetailsService implements UserDetailsService {

    // Injects UserRepository to fetch user data from the database
    @Autowired
    private UserRepository userRepository;

    // This method is called automatically by Spring Security
    // when a user tries to log in
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Fetch the user from database using username
        // If the user is not found, throw an exception
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert your User entity into Spring Security's UserDetails object
        return org.springframework.security.core.userdetails.User.builder()

                // Set username
                .username(user.getUsername())

                // Set password (should already be encrypted in database)
                .password(user.getPassword())

                // Set user roles/authorities
                // Spring Security expects roles in format: ROLE_ADMIN, ROLE_USER etc.
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole())
                ))

                // Build and return the UserDetails object
                .build();
    }
}

