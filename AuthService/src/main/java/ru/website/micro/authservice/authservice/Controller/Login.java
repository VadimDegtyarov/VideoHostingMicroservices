package ru.website.micro.authservice.authservice.Controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Tag(name = "User login",description = "Авторизация пользователя")
@RequiredArgsConstructor
@Controller
public class Login {


    private ClientRegistrationRepository clientRegistrationRepository;
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
