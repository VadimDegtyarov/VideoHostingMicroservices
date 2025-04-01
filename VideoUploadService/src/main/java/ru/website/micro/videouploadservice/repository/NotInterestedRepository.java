package ru.website.micro.videouploadservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videouploadservice.model.NotInterested;

public interface NotInterestedRepository extends JpaRepository<NotInterested, Long> {
}
