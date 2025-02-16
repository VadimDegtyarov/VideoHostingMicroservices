package ru.website.micro.authservice.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.authservice.userservice.dto.AddInfoUserDTO;
import ru.website.micro.authservice.userservice.model.User;
import ru.website.micro.authservice.userservice.model.UserAuthInfo;
import ru.website.micro.authservice.userservice.service.UserService;

import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PatchMapping("/{id}")
    public ResponseEntity<Void> addInfoUser(@RequestBody AddInfoUserDTO userDTO, @PathVariable UUID id){
        userService.updateUser(id, userDTO);
        return ResponseEntity.ok().build() ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<Void> subscribe(@RequestBody String loginTargetUser, Principal principal) {
        userService.subscribeUser(principal.getName(), loginTargetUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/subscriptions")
    public ResponseEntity<Void> unSubscribe(@RequestBody String loginTargetUser, Principal principal) {
        userService.unsubscribeUser(principal.getName(), loginTargetUser);
        return ResponseEntity.ok().build();
    }
}
