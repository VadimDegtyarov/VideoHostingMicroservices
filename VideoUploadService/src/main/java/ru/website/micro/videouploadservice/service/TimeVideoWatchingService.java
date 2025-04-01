package ru.website.micro.videouploadservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.website.micro.videouploadservice.model.TimeVideoWatching;
import ru.website.micro.videouploadservice.repository.TimeVideoWatchingRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeVideoWatchingService {
    private final TimeVideoWatchingRepository timeVideoWatchingRepository;
    private final UserVideoHelper userVideoHelper;

    public HttpStatus addingTimeWatching(UUID userId, Long videoId, Integer timeWatching) {
        TimeVideoWatching timeVideoWatching = TimeVideoWatching.builder()
                .video(userVideoHelper.getVideoById(videoId))
                .user(userVideoHelper.getUserById(userId))
                .duration(timeWatching)
                .build();
        timeVideoWatchingRepository.save(timeVideoWatching);
        return HttpStatus.OK;
    }

}
