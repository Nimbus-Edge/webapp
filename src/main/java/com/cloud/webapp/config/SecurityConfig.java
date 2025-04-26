package com.cloud.webapp.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/healthz/**").permitAll()
                        .requestMatchers("/cicd/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()  // Swagger API docs
                        .requestMatchers("/swagger-ui/**").permitAll()   // Swagger UI
                        .requestMatchers("/actuator/**").permitAll()     // Actuator endpoints
                        .requestMatchers("/api/**").permitAll()          // Emails
                        //Create user will be public, authless
                        .requestMatchers(HttpMethod.POST, "/v1/user").permitAll()
                        //Verify user will be public, authless
                        .requestMatchers(HttpMethod.GET, "/v1/user/verify").permitAll()
                        //Anything with GET/PUT user will be authenticated
                        .requestMatchers("/v1/user/**").authenticated()
                        //Rest all, default will be authenticated
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
