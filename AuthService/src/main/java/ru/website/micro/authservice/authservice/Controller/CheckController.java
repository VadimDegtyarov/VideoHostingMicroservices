package ru.website.micro.authservice.authservice.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.website.micro.authservice.authservice.service.UserAuthInfoService;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class CheckController {
    private final UserAuthInfoService userAuthInfoService;
    @GetMapping
    public ResponseEntity<String> getHello(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Hello, %s!".formatted(user.getUsername()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String exampleAdmin() {
        return "Hello Admin";
    }

    @GetMapping("/set-admin")
    public void exampleGetAdmin() {
        userAuthInfoService.setAdmin();
    }

}
