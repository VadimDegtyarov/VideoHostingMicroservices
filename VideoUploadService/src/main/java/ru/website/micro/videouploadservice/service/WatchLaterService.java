package ru.website.micro.videouploadservice.service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.website.micro.videouploadservice.model.WatchLater;
import ru.website.micro.videouploadservice.repository.WatchLaterRepository;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WatchLaterService {
    private final WatchLaterRepository watchLaterRepository;
    private final UserVideoHelper userVideoHelper;
    public HttpStatus addingWatchLater(UUID userId, Long videoId) {
        WatchLater watchLater = WatchLater.builder()
                .video(userVideoHelper.getVideoById(videoId))
                .user(userVideoHelper.getUserById(userId)).build();
        watchLaterRepository.save(watchLater);
        return HttpStatus.OK;
    }

}
