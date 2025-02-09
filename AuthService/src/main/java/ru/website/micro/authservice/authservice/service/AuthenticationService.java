package ru.website.micro.authservice.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.website.micro.authservice.authservice.dto.SignUpUserDTO;
import ru.website.micro.authservice.authservice.model.Role;
import ru.website.micro.authservice.authservice.model.UserAuthInfo;
import ru.website.micro.authservice.authservice.repository.RolesRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserAuthInfoService userAuthInfoService;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    public HttpStatus signUp(SignUpUserDTO sigUpUserDTO) {
        try {
            Optional<Role> userRole = rolesRepository.findByRole("USER_ROLE");
            UserAuthInfo myUser = UserAuthInfo.builder()
                    .phoneNumber(sigUpUserDTO.getPhoneNumber())
                    .roles(new ArrayList<>(List.of(userRole.get())))
                    .email(sigUpUserDTO.getEmail())
                    .passwordHash(passwordEncoder.encode(sigUpUserDTO.getPassword()))
                    .build();

            return userAuthInfoService.create(myUser);
        } catch (Exception e) {
            log.info("Ошибка регистрации:{}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

}
