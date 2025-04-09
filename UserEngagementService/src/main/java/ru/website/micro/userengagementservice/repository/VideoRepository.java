package ru.website.micro.userengagementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.userengagementservice.model.Video;


public interface VideoRepository extends JpaRepository<Video,Long> {
}
