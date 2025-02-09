package ru.website.micro.authservice.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.authservice.authservice.model.UserAuthInfo;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthRepository extends JpaRepository<UserAuthInfo, UUID> {

    Optional<UserAuthInfo> findByEmail(String email);

    Optional<UserAuthInfo> findByPhoneNumber(String phoneNumber);
}
