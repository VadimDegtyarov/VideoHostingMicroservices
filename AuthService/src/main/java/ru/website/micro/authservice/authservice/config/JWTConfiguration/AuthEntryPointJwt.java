package ru.website.micro.authservice.authservice.config.JWTConfiguration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@RequiredArgsConstructor
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String errorMessage = "Unauthorized";
        if (authException != null) {
            errorMessage = authException.getMessage();
        }
        logger.warn("Ошибка аутентификации: {}", errorMessage);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON);
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }

}
