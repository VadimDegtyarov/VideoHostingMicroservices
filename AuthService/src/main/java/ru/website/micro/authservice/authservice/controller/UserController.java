package ru.website.micro.authservice.authservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.authservice.authservice.config.JWTConfiguration.JWTUtil;
import ru.website.micro.authservice.authservice.dto.SignUpUserDTO;
import ru.website.micro.authservice.authservice.model.UserAuthInfo;
import ru.website.micro.authservice.authservice.service.AuthenticationService;
@Tag(name ="User controller",description = "Контроллер управления пользователями")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;

    @PostMapping("/create-user")
    public ResponseEntity<UserAuthInfo> signUp(@RequestBody @Valid SignUpUserDTO signUpUserDTO) {
        UserAuthInfo user = authenticationService.signUp(signUpUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
