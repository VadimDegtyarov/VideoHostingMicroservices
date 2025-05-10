package ru.website.micro.recommendationservice.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.recommendationservice.model.user.User;


import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
