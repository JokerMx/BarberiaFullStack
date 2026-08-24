package cl.Barberia.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SessionAuthenticationFilter sessionAuthenticationFilter() {
        return new SessionAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SessionAuthenticationFilter sessionAuthenticationFilter)
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/usuarios/registro").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/servicios/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/usuarios/username/**", "/api/usuarios/email/**").authenticated()
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/servicios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/servicios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/servicios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/reservas/*/estado").hasRole("ADMIN")
                .requestMatchers("/api/reservas/**").authenticated()
                .anyRequest().permitAll())
            .addFilterBefore(sessionAuthenticationFilter, AnonymousAuthenticationFilter.class);
        return http.build();
    }
}