package ru.website.micro.videouploadservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videouploadservice.model.VideoUserId;
import ru.website.micro.videouploadservice.model.WatchLater;

public interface WatchLaterRepository extends JpaRepository<WatchLater, VideoUserId> {
}
