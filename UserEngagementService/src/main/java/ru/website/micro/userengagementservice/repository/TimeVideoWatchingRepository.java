package ru.website.micro.userengagementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.website.micro.userengagementservice.model.TimeVideoWatching;
import ru.website.micro.userengagementservice.model.VideoUserId;


import java.util.Optional;
import java.util.UUID;


public interface TimeVideoWatchingRepository extends JpaRepository<TimeVideoWatching, VideoUserId> {
    Optional<TimeVideoWatching> findByUserIdAndVideoId(UUID userId, Long videoId);
}
