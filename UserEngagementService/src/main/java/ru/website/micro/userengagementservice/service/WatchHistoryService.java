package ru.website.micro.userengagementservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.website.micro.userengagementservice.exception.ResourceNotFoundException;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.model.VideoUserId;
import ru.website.micro.userengagementservice.model.WatchHistory;
import ru.website.micro.userengagementservice.model.user.User;
import ru.website.micro.userengagementservice.repository.WatchHistoryRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WatchHistoryService {
    private final WatchHistoryRepository watchHistoryRepository;
    private final UserVideoHelper userVideoHelper;

    @Transactional
    public boolean toggleWatchHistory(UUID userId, Long videoId, Integer progress) {
        User user = userVideoHelper.getUserById(userId);
        Video video = userVideoHelper.getVideoById(videoId);
        Optional<WatchHistory> existing = watchHistoryRepository.findByUserAndVideo(user, video);
        if (existing.isPresent()) {
            watchHistoryRepository.delete(existing.get());
            return false;
        } else {
            VideoUserId videoUserId = VideoUserId.builder()
                    .userId(userId)
                    .videoId(videoId).build();
            WatchHistory watchHistory = WatchHistory.builder()
                    .id(videoUserId)
                    .progress(progress)
                    .user(user)
                    .video(video)
                    .build();
            watchHistoryRepository.save(watchHistory);
            return true;
        }
    }

    public Page<Video> getWatchHistory(UUID userId, Pageable pageable, Long lastVideoId) {
        Page<Video> page = watchHistoryRepository.findWatchHistoryByUser(userId, lastVideoId, pageable);
        return page.isEmpty() ? null : page;
    }

    public Boolean isVideoInWatchHistory(UUID userId, Long videoId) {
        try {
            Optional<WatchHistory> watchHistory = watchHistoryRepository
                    .findByUserIdAndVideoId(userId, videoId);

            return watchHistory.isPresent();
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
