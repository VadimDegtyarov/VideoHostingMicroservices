package ru.website.micro.gateway.apigateway.JWTConfiguration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JWTUtil {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${jwt.cookie-token-key}")
    private String jwtSecretBase64;
    @Getter
    @Value("${token.signing.lifetime}")
    private Duration jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Секретный ключ jwt должен иметь длину не менее 256 бит(32 байта)");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            throw new JwtException("Ошибка парсинга jwt токена: " + e.getMessage());
        }
    }

    public Collection<? extends GrantedAuthority> getUserRoles(String authToken) throws JwtException {
        Claims claims = validateJwtToken(authToken);
        logger.info("Roles:%s".formatted(claims.get("roles").toString()));
        List<?> roles = claims.get("roles", List.class);

        return roles.stream()
                .map(role -> {
                    if (role instanceof LinkedHashMap) {
                        return new SimpleGrantedAuthority((String) ((LinkedHashMap<?, ?>) role).get("authority"));
                    } else {
                        return new SimpleGrantedAuthority(role.toString());
                    }
                })
                .collect(Collectors.toList());
    }

    public Claims validateJwtToken(String authToken) {
        try {
            return parseClaims(authToken);
        } catch (JwtException e) {
            logger.error("Ошибка валидации jwt токена: {}", e.getMessage());
            throw new JwtException("JWT token validation failed", e);
        }
    }
}
