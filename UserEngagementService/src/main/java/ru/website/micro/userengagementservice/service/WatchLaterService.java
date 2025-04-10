package ru.website.micro.userengagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.website.micro.userengagementservice.exception.ResourceNotFoundException;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.model.VideoUserId;
import ru.website.micro.userengagementservice.model.WatchHistory;
import ru.website.micro.userengagementservice.model.WatchLater;
import ru.website.micro.userengagementservice.model.user.User;
import ru.website.micro.userengagementservice.repository.WatchLaterRepository;


import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WatchLaterService {
    private final WatchLaterRepository watchLaterRepository;
    private final UserVideoHelper userVideoHelper;

    public WatchLater addingWatchLater(UUID userId, Long videoId) {
        User user = userVideoHelper.getUserById(userId);
        Video video = userVideoHelper.getVideoById(videoId);

        Optional<WatchLater> existing = watchLaterRepository.getWatchLaterByUserAndVideo(user, video);
        if (existing.isPresent()) {
            watchLaterRepository.delete(existing.get());
            return null;
        } else {
            VideoUserId videoUserId = VideoUserId.builder()
                    .userId(userId)
                    .videoId(videoId).build();
            WatchLater watchLater = WatchLater.builder()
                    .id(videoUserId)
                    .video(userVideoHelper.getVideoById(videoId))
                    .user(userVideoHelper.getUserById(userId)).build();

            return watchLaterRepository.save(watchLater);
        }

    }

    public Page<Video> getWatchLaterVideos(UUID userId, Pageable pageable, Long lastVideoId) {
        Page<Video> page = watchLaterRepository.findWatchHistoryByUser(userId, lastVideoId, pageable);
        return page.isEmpty() ? null : page;
    }

    public Boolean isVideoInWatchLater(UUID userId, Long videoId) {
        try {
            Optional<WatchLater> watchLater = watchLaterRepository
                    .findByUserIdAndVideoId(userId, videoId);

            return watchLater.isPresent();
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public void removeFromWatchLater(UUID userId, Long videoId) {
        watchLaterRepository.delete(watchLaterRepository.getWatchLaterByUserIdAndVideoId(userId, videoId).orElseThrow(
                () -> new ResourceNotFoundException("Видео у пользователя с id %s видео с id %s не добавлено в 'смотреть позже'"
                        .formatted(userId, videoId))));
    }

}
