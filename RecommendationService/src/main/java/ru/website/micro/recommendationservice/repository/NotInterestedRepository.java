package ru.website.micro.recommendationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.recommendationservice.model.Video;
import ru.website.micro.recommendationservice.model.user.User;
import ru.website.micro.recommendationservice.model.userInteractions.NotInterested;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotInterestedRepository extends JpaRepository<NotInterested, Long> {

    Optional<NotInterested> findByUserIdAndVideoId(UUID userId, Long videoId);

    Optional<NotInterested> findNotInterestedByUserAndVideo(User user, Video video);

    @Query("SELECT ni.id.videoId FROM not_interested ni WHERE ni.id.userId = :userId")
    List<Long> findTargetIdsByUser(@Param("userId") UUID userId);
}
