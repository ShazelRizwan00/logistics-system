package com.logistics.config;

import com.logistics.security.JwtAuthEntryPoint;
import com.logistics.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 *
 * Key decisions:
 *  - STATELESS sessions: no HttpSession created; JWT carries all state.
 *  - CSRF disabled: safe for stateless REST APIs (no browser cookie auth).
 *  - @EnableMethodSecurity: enables @PreAuthorize on service/controller methods
 *    for fine-grained per-method role checks beyond URL-level rules.
 *  - BCrypt with default strength (10 rounds): good balance of security vs CPU.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthEntryPoint       authEntryPoint;
    private final UserDetailsService      userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          JwtAuthEntryPoint authEntryPoint,
                          UserDetailsService userDetailsService) {
        this.jwtFilter         = jwtFilter;
        this.authEntryPoint    = authEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF – not needed for stateless JWT REST APIs
            .csrf(AbstractHttpConfigurer::disable)

            // Return 401 JSON instead of redirect for unauthenticated access
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))

            // No server-side sessions
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()

                // Admin-only management
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Customer endpoints
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET,  "/api/orders/my", "/api/orders/my/**")
                        .hasRole("CUSTOMER")

                // Delivery agent endpoints
                .requestMatchers(HttpMethod.GET, "/api/deliveries/my", "/api/deliveries/my/**")
                        .hasRole("DELIVERY_AGENT")
                .requestMatchers(HttpMethod.PATCH, "/api/deliveries/*/status")
                        .hasAnyRole("DELIVERY_AGENT", "ADMIN")

                // Shared authenticated endpoints
                .anyRequest().authenticated()
            )

            // H2 console uses iframes – allow same origin
            .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()))

            // Register our JWT filter before the standard username/password filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
