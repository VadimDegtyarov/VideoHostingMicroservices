package ru.website.micro.userengagementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.userengagementservice.model.Reaction;
import ru.website.micro.userengagementservice.model.ReactionId;

public interface ReactionRepository extends JpaRepository<Reaction, ReactionId> {
}
