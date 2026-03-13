package com.sabin.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component // Makes this class a Spring Bean so it can be injected anywhere
public class JwtUtil {

    private final SecretKey signingKey;
    private final long expiration;

    public JwtUtil(
            @org.springframework.beans.factory.annotation.Value("${jwt.secret:mySecretKeyForSigningTokens1234567890}") String secret,
            @org.springframework.beans.factory.annotation.Value("${jwt.expiration:86400000}") long expiration) {

        // Keep startup errors simple and clear if JWT config is missing or too weak.
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("jwt.secret cannot be empty");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret must be at least 32 characters");
        }

        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.expiration = expiration;
    }

    // Converts the secret string into a SecretKey object used by JWT
    private SecretKey getSigningKey() {
        return signingKey;
    }

    // Generates a JWT token for a given user
    public String generateToken(UserDetails userDetails) {

        // Claims are extra information stored inside the token
        Map<String, Object> claims = new HashMap<>();

        // username is used as the subject of the token
        return createToken(claims, userDetails.getUsername());
    }

    // Creates the JWT token with claims, subject, issue time, expiration and signature
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()

                // additional data stored in token
                .setClaims(claims)

                // usually the username
                .setSubject(subject)

                // token creation time
                .setIssuedAt(new Date(System.currentTimeMillis()))

                // token expiration time
                .setExpiration(new Date(System.currentTimeMillis() + expiration))

                // sign the token with secret key
                .signWith(getSigningKey())

                // generate compact JWT string
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract any claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        // get all claims first
        final Claims claims = extractAllClaims(token);

        // apply function to extract specific data
        return claimsResolver.apply(claims);
    }

    // Parses the JWT and extracts all claims from it
    private Claims extractAllClaims(String token) {

        return Jwts.parser()

                // verify token using the same signing key
                .verifyWith(getSigningKey())

                // build parser
                .build()

                // parse the token and verify signature
                .parseSignedClaims(token)

                // return payload (claims inside token)
                .getPayload();
    }

    // Checks whether token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validates token:
    // 1. username inside token must match
    // 2. token should not be expired
    public Boolean validateToken(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

