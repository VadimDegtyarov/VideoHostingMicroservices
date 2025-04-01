package ru.website.micro.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.userservice.dto.AddInfoUserDTO;
import ru.website.micro.userservice.model.Image;
import ru.website.micro.userservice.model.User;
import ru.website.micro.userservice.service.UserService;

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
    @PostMapping("/{id}/avatar")
    public void uploadAvatar(@PathVariable UUID id, @ModelAttribute Image image)
    {
        userService.uploadImage(id,image);

    }
    @GetMapping("/{id}/avatar")
    public ResponseEntity<InputStreamResource> getAvatar(@PathVariable UUID id)
    {
         return userService.getAvatar(id);
    }
}
