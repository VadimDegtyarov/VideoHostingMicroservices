package ru.website.micro.authservice.authservice.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CheckNonREST {
    @GetMapping("/ex")
    public String ex() {
        return "user-list";
    }
}
