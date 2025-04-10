package ru.website.micro.userengagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.website.micro.userengagementservice.exception.ResourceNotFoundException;
import ru.website.micro.userengagementservice.model.NotInterested;
import ru.website.micro.userengagementservice.model.TimeVideoWatching;
import ru.website.micro.userengagementservice.model.VideoUserId;
import ru.website.micro.userengagementservice.repository.TimeVideoWatchingRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeVideoWatchingService {
    private final TimeVideoWatchingRepository timeVideoWatchingRepository;
    private final UserVideoHelper userVideoHelper;

    public TimeVideoWatching addingTimeWatching(UUID userId, Long videoId, Integer timeWatching) {
        VideoUserId videoUserId = VideoUserId.builder()
                .userId(userId)
                .videoId(videoId).build();
        TimeVideoWatching timeVideoWatching = TimeVideoWatching.builder()
                .id(videoUserId)
                .video(userVideoHelper.getVideoById(videoId))
                .user(userVideoHelper.getUserById(userId))
                .duration(timeWatching)
                .build();

        return timeVideoWatchingRepository.save(timeVideoWatching);

    }

    public TimeVideoWatching updateTimeWatching(UUID userId, Long videoId, Integer timeWatching) {
        TimeVideoWatching timeVideoWatching = timeVideoWatchingRepository.findByUserIdAndVideoId(userId, videoId).orElseThrow(() -> new ResourceNotFoundException("Время просмотра ранее не отслеживалось"));
        timeVideoWatching.setDuration(timeWatching);

        return timeVideoWatchingRepository.save(timeVideoWatching);
    }

    public Integer getTimeWatching(UUID userId, Long videoId) {
        try {
            Optional<TimeVideoWatching> timeVideoWatching = timeVideoWatchingRepository
                    .findByUserIdAndVideoId(userId, videoId);

            return timeVideoWatching.get().getDuration();
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
