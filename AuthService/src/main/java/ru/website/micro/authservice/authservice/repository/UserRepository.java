package ru.website.micro.authservice.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.authservice.authservice.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
