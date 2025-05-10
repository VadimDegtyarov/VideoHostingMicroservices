package ru.website.micro.recommendationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.recommendationservice.model.user.VideoUserId;
import ru.website.micro.recommendationservice.model.userInteractions.TimeVideoWatching;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public interface TimeVideoWatchingRepository extends JpaRepository<TimeVideoWatching, VideoUserId> {
    Optional<TimeVideoWatching> findByUserIdAndVideoId(UUID userId, Long videoId);

    @Query("SELECT tv.id.videoId AS videoId, tv.duration AS duration " +
           "FROM TimeVideoWatching tv " +
           "WHERE tv.id.userId = :userId")
    List<VideoDuration> findDurationsByUser(@Param("userId") UUID userId);

    interface VideoDuration {
        Long getVideoId();

        Integer getDuration();
    }
}
