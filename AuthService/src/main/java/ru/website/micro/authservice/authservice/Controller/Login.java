package ru.website.micro.authservice.authservice.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class Login {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
