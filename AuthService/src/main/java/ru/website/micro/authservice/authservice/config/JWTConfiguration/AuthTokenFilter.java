package ru.website.micro.authservice.authservice.config.JWTConfiguration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.website.micro.authservice.authservice.Exception.ResourceNotFoundException;
import ru.website.micro.authservice.authservice.model.TokenUserAuthInfo;
import ru.website.micro.authservice.authservice.model.UserAuthInfo;
import ru.website.micro.authservice.authservice.service.UserAuthInfoService;

import java.io.IOException;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    private final UserAuthInfoService userAuthInfoService;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            logger.info("jwt:{}", jwt);
            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                authenticateUser(jwt, request);
            }
        } catch (Exception e) {
            logger.error("Ошибка аутентификации пользователя:{}\n", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String userName = jwtUtil.getUserNameFromJwtToken(jwt);
        UserAuthInfo userAuthInfo = userAuthInfoService.getByLogin(userName);
        TokenUserAuthInfo userDetails = new TokenUserAuthInfo(userAuthInfo.getId(),
                userAuthInfo.getEmail(), userAuthInfo.getPhoneNumber(), userAuthInfo.getPasswordHash(),
                userAuthInfo.getRoles(), jwt);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        logger.info("SetPrincipal: {}",authentication.getPrincipal().toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String parseJwt(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Stream.of(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("__JWT-auth-token"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

        }
        return null;
    }
}
