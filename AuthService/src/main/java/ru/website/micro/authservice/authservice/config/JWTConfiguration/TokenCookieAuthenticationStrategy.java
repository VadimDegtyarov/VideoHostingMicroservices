package ru.website.micro.authservice.authservice.config.JWTConfiguration;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
@RequiredArgsConstructor
public class TokenCookieAuthenticationStrategy implements SessionAuthenticationStrategy {

    private final JWTUtil jwtUtil;
    private final Logger logger = LoggerFactory.getLogger(TokenCookieAuthenticationStrategy.class);
    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            try {
                logger.info("auth_info:{}",authentication.getPrincipal().toString());
                String jwt = jwtUtil.generateJwtToken(authentication);
                Cookie cookie = new Cookie("__JWT-auth-token", jwt);
                cookie.setPath("/");
                cookie.setDomain(null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setMaxAge((int) ChronoUnit.SECONDS.between(Instant.now(), Instant.now().plus(jwtUtil.getJwtExpiration())));
                response.addCookie(cookie);
            }
            catch (Exception e) {
                logger.info("Ошибка создания куки:{}",e.getMessage(),e);
                throw new SessionAuthenticationException(e.getMessage());
            }

        }

    }
}
