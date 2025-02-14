package ru.website.micro.authservice.authservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.website.micro.authservice.authservice.dto.SignUpUserDTO;
import ru.website.micro.authservice.authservice.service.AuthenticationService;
@Tag(name ="User controller",description = "Контроллер управления пользователями")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;
    @PostMapping("/create-user")
    public HttpStatus signUp(@RequestBody @Valid SignUpUserDTO signUpUserDTO) {
        return authenticationService.signUp(signUpUserDTO);
    }
}
