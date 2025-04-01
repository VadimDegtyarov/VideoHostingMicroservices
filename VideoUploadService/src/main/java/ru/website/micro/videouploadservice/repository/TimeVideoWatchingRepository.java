package ru.website.micro.videouploadservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videouploadservice.model.TimeVideoWatching;
import ru.website.micro.videouploadservice.model.VideoUserId;


public interface TimeVideoWatchingRepository extends JpaRepository<TimeVideoWatching, VideoUserId> {
}
