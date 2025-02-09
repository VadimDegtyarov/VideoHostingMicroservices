package ru.website.micro.authservice.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.authservice.authservice.model.Role;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByRole(String name);
}
