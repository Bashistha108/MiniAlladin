package com.mini.alladin.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * This class is responsible for:
 *  - Creating JWT tokens after login
 *  - Validating incoming JWT tokens
 *  - Extracting user info (email) from token
 *
 *
 *  To generate and validate JWT tokens
 */

/**
 * The person logs in using Google.
 * Backend checks if this person already exists in DB.
 * If not, add them to list and give them the TRADER role.
 * Give them a unique JWT token.
 * */
@Component
public class JwtTokenProvider {
    // From application.properties
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-ms}")
    private int jwtExpirationMs;



    // This method is used to create the secret key used to sign (and later verify) your JWTs.
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token for authenticated user
     * @param userDetails user info from Spring Security
     * @return signed JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Extract user's email (username) from JWT token
     * @param token the JWT token
     * @return email (subject of the token)
     * Used when we want to identify who made a request, based on their token.
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate if the token is valid (signature and expiration)
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }catch (SignatureException e) {
            System.out.println("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }



}
