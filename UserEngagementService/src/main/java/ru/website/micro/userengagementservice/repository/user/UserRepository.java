package ru.website.micro.userengagementservice.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.userengagementservice.model.user.User;


import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
