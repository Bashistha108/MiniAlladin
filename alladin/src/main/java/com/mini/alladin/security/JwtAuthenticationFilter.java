package com.mini.alladin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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


/*
   ---------2---------
   Custom filter that runs before Spring’s built-in authentication.
   Purpose: Check if JWT exists → validate it → set user as authenticated

   Added to SecurityConfig:
   .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
*/
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // to validate tokens and extract email from token
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // to load user from db using email found in token
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /*
         This method is triggered on every request.
         It checks: "Is there a valid JWT token? If yes, log the user in."
        */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/ws") || path.contains("websocket") || path.contains("xhr") || path.contains("info")) {
            filterChain.doFilter(request, response);
            return;
        }


        if (path.startsWith("/api/users/unblock/")) {
            // Skip filter for unblock link (public endpoint)
            filterChain.doFilter(request, response);
            return;
        }


        String token = null;

        // First try to get token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // remove "Bearer " part
        }

        // If not found in header check in cookies
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) { // same name you used in OAuth2LoginSuccessHandler
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        //  If token found and valid- > authenticate the user
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // Extract email from token
            String email = jwtTokenProvider.getEmailFromToken(token);

            // Load user from DB
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            //  Blocked user? Redirect to login with error (isActive in db) -> isActive mapped with isEnabled in CustomUserDetails
            if (!userDetails.isEnabled()) {
                request.getRequestDispatcher("/login?blocked=true").forward(request, response);
                return;
            }

            // Create Spring authentication object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            // Attach details like IP, browser info
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Tell Spring Security: "This user is now logged in"
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue with next filter or controller
        filterChain.doFilter(request, response);
    }

}