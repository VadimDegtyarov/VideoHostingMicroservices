package ru.website.micro.authservice.authservice.config.JWTConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ru.website.micro.authservice.authservice.model.UserAuthInfo;
import ru.website.micro.authservice.authservice.service.UserAuthInfoService;

@Component
@RequiredArgsConstructor
public class JWTUtil {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    private final UserAuthInfoService userAuthInfoService;

    @Value("${jwt.cookie-token-key}")
    private String jwtSecretBase64;
    @Getter
    @Value("${token.signing.lifetime}")
    private Duration jwtExpiration;

    private static final String CLAIM_ID = "id";
    private static final String CLAIM_SUB = "sub";
    private static final String CLAIM_ROLES = "roles";

    public String generateJwtToken(Authentication authentication) {
        try {
            UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
            UserAuthInfo userAuthInfo = userAuthInfoService.getByLogin(userPrincipal.getUsername());
            Instant now = Instant.now();
            return Jwts.builder()
                    .claim(CLAIM_ID, userAuthInfo.getId())
                    .claim(CLAIM_SUB, userPrincipal.getUsername())
                    .claim(CLAIM_ROLES, userPrincipal.getAuthorities())
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plus(this.jwtExpiration)))
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            logger.error("Ошибка генерации JWT токена: {}", e.getMessage(), e);
            throw new AuthenticationServiceException("Ошибка генерации JWT токена", e);
        }
    }

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
        } catch (Exception e) {
            throw new JwtException("Ошибка парсинга jwt токена: " + e.getMessage());
        }
    }

    public String getUserNameFromJwtToken(String token) {
        return parseClaims(token).get("sub", String.class);
    }

    public Date getExpirationAtFromJwtToken(String token) {
        return parseClaims(token).getExpiration();
    }

    public UUID getUserIdFromJwtToken(String token) {
        return parseClaims(token).get("id", UUID.class);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            parseClaims(authToken);
            return true;
        } catch (JwtException e) {
            logger.error("Ошибка валидации jwt токена: {}", e.getMessage());
        }
        return false;
    }
}
