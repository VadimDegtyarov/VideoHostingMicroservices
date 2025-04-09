package ru.website.micro.userengagementservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.model.VideoUserId;
import ru.website.micro.userengagementservice.model.WatchHistory;
import ru.website.micro.userengagementservice.model.user.User;

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
            @Param("lastVideoId") Long lastVideoId,
            Pageable pageable);
}
