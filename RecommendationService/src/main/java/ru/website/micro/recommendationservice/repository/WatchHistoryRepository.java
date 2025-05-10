package ru.website.micro.recommendationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.recommendationservice.model.Video;
import ru.website.micro.recommendationservice.model.user.User;
import ru.website.micro.recommendationservice.model.user.VideoUserId;
import ru.website.micro.recommendationservice.model.userInteractions.WatchHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, VideoUserId> {
    Optional<WatchHistory> findByUserAndVideo(User user, Video video);

    Optional<WatchHistory> findByUserIdAndVideoId(UUID userId, Long videoId);

    @Query("""
            SELECT wh.video
            FROM WatchHistory wh
            WHERE wh.user.id = :userId
            AND (:lastVideoId IS NULL OR wh.video.id < :lastVideoId)
            ORDER BY wh.video.id DESC
            """)
    Page<Video> findWatchHistoryByUser(
            @Param("userId") UUID userId,
            @Param("lastVideoId") Long lastTargetId,
            Pageable pageable);

    boolean existsById(VideoUserId id);

    @Query("SELECT wh.video.id FROM WatchHistory wh WHERE wh.user.id = :userId")
    List<Long> findTargetIdsByUser(@Param("userId") UUID userId);
}
