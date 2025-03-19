package ru.website.micro.authservice.videouploadservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.authservice.userservice.model.video.Video;

public interface VideoRepository extends JpaRepository<Video,Long> {
}
