package ru.website.micro.authservice.authservice.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import ru.website.micro.authservice.authservice.config.JWTConfiguration.*;
import ru.website.micro.authservice.authservice.model.DeactivatedToken;
import ru.website.micro.authservice.authservice.model.TokenUserAuthInfo;
import ru.website.micro.authservice.authservice.model.UserAuthInfo;
import ru.website.micro.authservice.authservice.repository.DeactivatedTokenRepository;
import ru.website.micro.authservice.authservice.service.UserAuthInfoService;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration  {
    private final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    private final DeactivatedTokenRepository deactivatedTokenRepository;
    private final UserAuthInfoService userAuthInfoService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthTokenFilter authJwtTokenFilter(JWTUtil jwtUtil, @Lazy UserAuthInfoService userAuthInfoService) {
        return new AuthTokenFilter(userAuthInfoService, jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        TokenCookieAuthenticationStrategy tokenCookieAuthStrategy = new TokenCookieAuthenticationStrategy(jwtUtil);
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/ex")
                        .permitAll()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionAuthenticationStrategy(tokenCookieAuthStrategy))
                .authorizeHttpRequests(request -> request
                        .anyRequest().permitAll())
                .authenticationProvider(authProvider())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(
                        authJwtTokenFilter(jwtUtil, userAuthInfoService),
                        UsernamePasswordAuthenticationFilter.class
                                )
                .oauth2Login(oAuth2->
                        oAuth2.loginPage("/login")
                                .defaultSuccessUrl("/")
                        );
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(new CookieClearingLogoutHandler("__HOST-auth-token"))
                .addLogoutHandler((request, response, authentication) -> {
                    logger.info("authentication class:{}", authentication);
                    if (authentication != null && authentication.getPrincipal() instanceof TokenUserAuthInfo token) {
                        logger.info("principal:{}", authentication.getPrincipal().toString());
                        deactivatedTokenRepository.save(
                                new DeactivatedToken(jwtUtil.getUserIdFromJwtToken(token.getToken()),
                                        jwtUtil.getExpirationAtFromJwtToken(token.getToken())));
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    }
                })
        );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userAuthInfoService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
