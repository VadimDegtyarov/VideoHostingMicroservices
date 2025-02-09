package ru.website.micro.authservice.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.authservice.authservice.model.DeactivatedToken;

import java.util.UUID;

public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, UUID> {
}
