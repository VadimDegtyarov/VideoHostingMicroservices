package ru.website.micro.userengagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.website.micro.userengagementservice.dto.NotInterestedDto;
import ru.website.micro.userengagementservice.exception.ResourceNotFoundException;
import ru.website.micro.userengagementservice.model.NotInterested;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.model.VideoUserId;
import ru.website.micro.userengagementservice.model.WatchLater;
import ru.website.micro.userengagementservice.model.user.User;
import ru.website.micro.userengagementservice.repository.NotInterestedRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotInterestedService {
    private final NotInterestedRepository notInterestedRepository;
    private final UserVideoHelper userVideoHelper;

    public boolean toggleNotInterested(UUID userId, Long videoId) {
        User user = userVideoHelper.getUserById(userId);
        Video video = userVideoHelper.getVideoById(videoId);

        Optional<NotInterested> existing = notInterestedRepository.findNotInterestedByUserAndVideo(user, video);

        if (existing.isPresent()) {
            notInterestedRepository.delete(existing.get());
            return false;
        } else {
            VideoUserId videoUserId = VideoUserId.builder()
                    .userId(userId)
                    .videoId(videoId)
                    .build();

            NotInterested notInterested = NotInterested.builder()
                    .id(videoUserId)
                    .video(video)
                    .user(user)
                    .build();

            notInterestedRepository.save(notInterested);
            return true;
        }
    }

    public Boolean isVideoInNotInterested(UUID userId, Long videoId) {
        try {
            Optional<NotInterested> notInterested = notInterestedRepository
                    .findByUserIdAndVideoId(userId, videoId);

            return notInterested.isPresent();
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
