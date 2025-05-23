package ru.website.micro.videouploadservice.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videouploadservice.model.user.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
