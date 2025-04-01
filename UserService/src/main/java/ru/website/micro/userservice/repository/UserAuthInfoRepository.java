package ru.website.micro.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.userservice.model.UserAuthInfo;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthInfoRepository extends JpaRepository<UserAuthInfo, UUID> {

    Optional<UserAuthInfo> findByEmail(String email);
    Optional<UserAuthInfo> findByPhoneNumber(String phoneNumber);
}
