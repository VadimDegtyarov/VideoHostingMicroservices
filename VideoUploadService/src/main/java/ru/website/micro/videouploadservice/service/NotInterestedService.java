package ru.website.micro.videouploadservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.website.micro.videouploadservice.model.NotInterested;
import ru.website.micro.videouploadservice.model.WatchLater;
import ru.website.micro.videouploadservice.repository.NotInterestedRepository;
import ru.website.micro.videouploadservice.repository.VideoRepository;
import ru.website.micro.videouploadservice.repository.user.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotInterestedService {
    private final NotInterestedRepository notInterestedRepository;
    private final UserVideoHelper userVideoHelper;

    public HttpStatus addingWatchLater(UUID userId, Long videoId) {
        NotInterested notInterested = NotInterested.builder()
                .video(userVideoHelper.getVideoById(videoId))
                .user(userVideoHelper.getUserById(userId)).build();
        notInterestedRepository.save(notInterested);
        return HttpStatus.OK;
    }
}
