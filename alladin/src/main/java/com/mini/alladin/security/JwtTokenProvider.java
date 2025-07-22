package com.mini.alladin.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * ---------------------------------------------------------
 * JwtTokenProvider
 * ---------------------------------------------------------
 * This class is responsible for all operations related to
 * JSON Web Tokens (JWT) in the Mini-Alladin application.
 *
 * Responsibilities:
 *  - Generate JWT tokens after successful authentication
 *  - Validate JWT tokens on incoming requests
 *  - Extract user details (email) from tokens
 *
 * JWT ensures stateless authentication by embedding
 *     user identity (email) into a digitally signed token.
 *
 * This class uses:
 *  - A secret signing key from application.properties
 *  - An expiration time to control token validity
 *
 * Used in:
 *  - AuthController (manual login)
 *  - OAuth2LoginSuccessHandler (Google login)
 *  - JwtAuthenticationFilter (token validation on every request)
 *
 * NOTE:
 *  - Tokens are signed with HMAC SHA512
 *  - Token structure: header.payload.signature
 *  - If the signature is invalid or token is expired, it is rejected
 *
 * @author Bashistha Joshi
 */

@Component
public class JwtTokenProvider {
    // From application.properties
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-ms}")
    private int jwtExpirationMs;



    // This method is used to create the secret key used to sign (and later verify) your JWTs.
    // In this program we have not used this method but in real life project should be used instead of using secret key directly
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
                // better would be .signWith(getSigningKey(), SignatureAlgorithm.HS512)
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
