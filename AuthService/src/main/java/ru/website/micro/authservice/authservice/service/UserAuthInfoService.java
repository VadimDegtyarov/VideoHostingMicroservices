package ru.website.micro.authservice.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import ru.website.micro.authservice.authservice.Exception.ResourceNotFoundException;
import ru.website.micro.authservice.authservice.dto.SignInUserDTO;
import ru.website.micro.authservice.authservice.model.Role;
import ru.website.micro.authservice.authservice.model.UserAuthInfo;
import ru.website.micro.authservice.authservice.repository.RolesRepository;
import ru.website.micro.authservice.authservice.repository.UserAuthRepository;

import java.util.stream.Collectors;


@Service
public class UserAuthInfoService implements UserDetailsService {
    private final UserAuthRepository userAuthRepository;
    final Logger logger = LoggerFactory.getLogger(UserAuthInfoService.class);
    private final RolesRepository rolesRepository;

    public UserAuthInfoService(UserAuthRepository userAuthRepository, @Lazy PasswordEncoder passwordEncoder, RolesRepository rolesRepository) {
        this.userAuthRepository = userAuthRepository;
        this.rolesRepository = rolesRepository;
    }

    private UserDetails loadByLogin(String login) throws UsernameNotFoundException {
        UserAuthInfo myUser = this.getByLogin(login);
        return new User(
                login,
                myUser.getPasswordHash(),
                myUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList())
        );
    }

    public UserAuthInfo getByLogin(String login) throws UsernameNotFoundException {
        UserAuthInfo myUser;
        myUser = login.contains("@") ? userAuthRepository.findByEmail(login).orElse(null)
                : userAuthRepository.findByPhoneNumber(login).orElse(null);
        boolean isEmail = login.contains("@");
        String loginName = isEmail ? "email" : "phone";
        if (myUser == null) {
            logger.warn("Unknown user with {}: {}", loginName, login);
            throw new UsernameNotFoundException(String.format("Unknown user with %s: %s", loginName, login));
        }
        return myUser;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return loadByLogin(login);
    }

    private HttpStatus save(UserAuthInfo userAuthInfo) {
        userAuthRepository.save(userAuthInfo);
        return HttpStatus.OK;
    }

    public HttpStatus create(UserAuthInfo userAuthInfo) {
        try {
            if (userAuthRepository.findByEmail(userAuthInfo.getEmail()).isPresent()) {
                throw new ResourceNotFoundException("Данный пользователь уже существует");
            }
            if (userAuthRepository.findByPhoneNumber(userAuthInfo.getPhoneNumber()).isPresent()) {
                throw new ResourceNotFoundException("Данный бользователь уже существует");
            }

            return save(userAuthInfo);
        } catch (Exception e) {
            logger.error("Ошибка создания пользователя:{}\n", e.getMessage(), e);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    public UserAuthInfo findByEmail(String email) {
        return userAuthRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь с адресом %s не найден", email)));
    }

    public UserDetailsService userDetailsService() {
        return this::getByLogin;
    }

    public UserAuthInfo getCurrentUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByEmail(userEmail);
    }

    public void setAdmin(HttpServletRequest request) {
        UserAuthInfo user = getCurrentUser();
        Role role = rolesRepository.getByRole("ROLE_ADMIN");
        user.addRole(role);
        userAuthRepository.save(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        logger.info("SetAdmin: {}", authentication.getPrincipal().toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
}
