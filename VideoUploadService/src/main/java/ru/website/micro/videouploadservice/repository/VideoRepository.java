package ru.website.micro.videouploadservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videouploadservice.model.Video;


public interface VideoRepository extends JpaRepository<Video,Long> {
}
