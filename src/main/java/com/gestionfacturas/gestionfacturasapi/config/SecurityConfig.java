package com.gestionfacturas.gestionfacturasapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()  // Permitir todas las solicitudes sin autenticación
                )
                .csrf(AbstractHttpConfigurer::disable)  // Deshabilitar CSRF
                .formLogin(AbstractHttpConfigurer::disable)  // Deshabilitar el formulario de login
                .httpBasic(AbstractHttpConfigurer::disable);  // Deshabilitar autenticación básica
        return http.build();
    }
}
