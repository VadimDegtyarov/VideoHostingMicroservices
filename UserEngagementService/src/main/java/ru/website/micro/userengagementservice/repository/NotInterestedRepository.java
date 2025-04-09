package ru.website.micro.userengagementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.userengagementservice.model.NotInterested;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.model.user.User;


import java.util.Optional;
import java.util.UUID;

public interface NotInterestedRepository extends JpaRepository<NotInterested, Long> {

    Optional<NotInterested> findByUserIdAndVideoId(UUID userId, Long videoId);

    Optional<NotInterested> findNotInterestedByUserAndVideo(User user, Video video);
}
