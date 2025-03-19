package ru.website.micro.gateway.apigateway.JWTConfiguration;


import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;


@RequiredArgsConstructor
@Component
public class JwtCookieAuthenticationFilter implements WebFilter, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(JwtCookieAuthenticationFilter.class);
    private final JWTUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().startsWith("/authservice")) {
            return chain.filter(exchange);
        }
        try {
            String jwt = parseJwt(exchange);
            if (jwt != null) {
                logger.info("jwt:{}", jwt);
                Claims claims = jwtUtil.validateJwtToken(jwt);
                if (claims != null) {

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), null, jwtUtil.getUserRoles(jwt)
                    );
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);

                    return chain.filter(exchange)
                            .contextWrite(ctx -> withAuthentication( authentication));
                } else {
                    return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);

                }
            } else {
                return chain.filter(exchange);
            }
        } catch (Exception e) {
            logger.error("Ошибка валидации jwt токена:{}\n", e.getMessage(), e);
            return onError(exchange, "JWT validation error", HttpStatus.UNAUTHORIZED);
        }
    }



    @Override
    public int getOrder() {
        return -1;  // Фильтр должен выполняться перед основной обработкой запроса
    }

    private String parseJwt(ServerWebExchange exchange) {
        try {
            if (exchange.getRequest().getCookies() != null &&
                    exchange.getRequest().getCookies().getFirst("__JWT-auth-token") != null) {
                return exchange.getRequest().getCookies().getFirst("__JWT-auth-token").getValue();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to parse JWT from cookie", e);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        logger.error(err);
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}
