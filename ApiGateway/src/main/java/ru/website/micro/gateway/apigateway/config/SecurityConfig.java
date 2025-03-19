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
import reactor.core.publisher.Mono;
import ru.website.micro.gateway.apigateway.Errors.NoPopupAuthenticationEntryPoint;
import ru.website.micro.gateway.apigateway.JWTConfiguration.JwtCookieAuthenticationFilter;
@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;
    private final NoPopupAuthenticationEntryPoint noPopupAuthenticationEntryPoint;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/authservice/login").permitAll()
                        .pathMatchers("/userservice/**").hasAnyRole("USER")
                        .pathMatchers("/authservice/auth/**").permitAll()
                        .pathMatchers("/authservice/swagger-ui/**", "/authservice/v3/api-docs/**").permitAll() // Swagger
                        .anyExchange().authenticated() // Все остальные запросы требуют аутентификации
                )
                .addFilterBefore(jwtCookieAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.authenticationEntryPoint(noPopupAuthenticationEntryPoint)
                )
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Отключите сессии
                .build();

    }
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> Mono.empty();
    }
}