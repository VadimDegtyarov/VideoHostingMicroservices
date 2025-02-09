package ru.website.micro.authservice.authservice.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.website.micro.authservice.authservice.dto.SignUpUserDTO;
import ru.website.micro.authservice.authservice.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    @PostMapping("/create-user")
    public HttpStatus signUp(@RequestBody @Valid SignUpUserDTO signUpUserDTO) {
        return authenticationService.signUp(signUpUserDTO);
    }
}
