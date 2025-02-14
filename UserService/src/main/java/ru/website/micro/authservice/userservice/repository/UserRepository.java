package ru.website.micro.authservice.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.authservice.userservice.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
