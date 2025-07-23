package com.mini.alladin.security;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
   ---------1---------
   Heart of security.
   Main configuration class where we define:
   - Public / protected / role-based endpoints
   - Enable JWT and OAuth2
   - Register filters
   - Disable session-based login
   - Hook in login success handlers

   Always create this first when setting up Spring Security.
   Everything else connects here.
*/
@Configuration
public class SecurityConfig {

    // Bean to encrypt passwords using BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /*
       Core method that tells Spring Security how to secure the application.

       Defines:
       - Which endpoints are public, protected, or role-restricted
       - What login methods to support (Form Login, Google OAuth2, JWT)
       - Logout behavior
       - Stateless vs Session
       - Which custom filters to include

       CSRF (Cross Site Request Forgery):
       - Normally protects against cookie/session attacks
       - Not needed when using stateless JWT, so we disable it
   */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF — we're using JWT, not sessions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless: no server sessions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/auth/login", "/register",
                                         "/auth/register", "/oauth2/**","/",
                                         "/api/users/unblock/**")
                                            .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/trader/**").hasRole("TRADER")
                        .requestMatchers("/api/stocks/**", "/admin/manage-stocks/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2  // Enable Google login
                        .loginPage("/login") // Use our custom login page instead of default Spring form
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // After Google login, fetch user info with our service
                        .successHandler(oAuth2LoginSuccessHandler) // What to do after login success (e.g. create user, set JWT)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // optional, default is /logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Delete JWT cookie
                            Cookie cookie = new Cookie("jwt", null); // Same name as the JWT cookie
                            cookie.setPath("/"); // Match path so browser knows which cookie to replace
                            cookie.setMaxAge(0); // Expire immediately
                            response.addCookie(cookie); // Add the "delete cookie" to response

                            response.sendRedirect("/login");// Redirect to login page after logout
                        })
                )
                .authenticationProvider(daoAuthenticationProvider()) // Support form login via DB (using UserDetailsService + PasswordEncoder)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Validate JWT before Spring handles login
        return http.build();
    }

    /*
       Expose AuthenticationManager as a Spring bean so we can @Autowired it in AuthController.
       Required for manual login: authenticationManager.authenticate(...)
    */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
      Tells Spring how to verify login credentials (email/password).
      Uses our custom user details service + password encoder.
   */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
