package com.prisme.back.config;

import com.prisme.back.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Activation CORS avec la configuration centralisée
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Désactivation CSRF pour API stateless
                .csrf(csrf -> csrf.disable())
                // Règles d'autorisation (issues de la première version)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/adresses").permitAll()
                        .requestMatchers("/api/adresses/**").authenticated()
                        .anyRequest().authenticated()
                )
                // Session stateless (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Filtre JWT avant l'authentification standard
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Fusion des origines :
        // - Version 1 : http://localhost:5173
        // - Version 2 : * (mais incompatible avec credentials)
        // - Version 3 : allowedOriginPatterns = "*" (compatible avec credentials)
        // On adopte allowedOriginPatterns = "*" pour accepter toutes les origines avec credentials
        config.setAllowedOriginPatterns(List.of("*"));

        // Méthodes autorisées : reprise de la version 3 (complète)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Headers autorisés : toutes les versions utilisent "*"
        config.setAllowedHeaders(List.of("*"));

        // Credentials : vrai pour les versions 1 et 3, faux pour la 2. On garde vrai (plus permissif)
        config.setAllowCredentials(true);

        // Durée de cache preflight : 3600s (versions 1 et 3)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Bean de la version 1
        return new BCryptPasswordEncoder();
    }
}