package ru.website.micro.authservice.userservice.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.website.micro.authservice.userservice.model.User;
import ru.website.micro.authservice.userservice.repository.UserRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserByEmail(String email) {}
}
