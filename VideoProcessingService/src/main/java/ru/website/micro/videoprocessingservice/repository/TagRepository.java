package ru.website.micro.videoprocessingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videoprocessingservice.model.Tag;

import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag,Long> {
    Optional<Tag> findByName(String name);
}
