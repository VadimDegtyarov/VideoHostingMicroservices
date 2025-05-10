package ru.website.micro.gateway.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import ru.website.micro.gateway.apigateway.Errors.NoPopupAuthenticationEntryPoint;
import ru.website.micro.gateway.apigateway.JWTConfiguration.JwtCookieAuthenticationFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;
    private final NoPopupAuthenticationEntryPoint noPopupAuthenticationEntryPoint;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors().and()  // Enable CORS using CorsWebFilter
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/authservice/login", "/authservice/api/v1/user/create-user", "/recommendationservice/api/v1/**").permitAll()
                        .pathMatchers("/userservice/**").hasRole("USER")
                        .pathMatchers("/authservice/auth/**").permitAll()
                        .pathMatchers("/authservice/swagger-ui/**", "/authservice/v3/api-docs/**").permitAll()
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtCookieAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(spec -> spec.authenticationEntryPoint(noPopupAuthenticationEntryPoint))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> Mono.empty();
    }
}
