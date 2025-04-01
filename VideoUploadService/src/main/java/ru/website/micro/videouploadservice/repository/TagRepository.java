package ru.website.micro.videouploadservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videouploadservice.model.Tag;

import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag,Long> {
    Optional<Tag> findByName(String name);
}
