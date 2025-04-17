package ru.website.micro.videoprocessingservice.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videoprocessingservice.model.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
