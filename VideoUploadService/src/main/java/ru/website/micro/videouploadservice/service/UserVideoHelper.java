package ru.website.micro.videouploadservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.website.micro.videouploadservice.exception.ResourceNotFoundException;
import ru.website.micro.videouploadservice.model.Video;
import ru.website.micro.videouploadservice.model.user.User;
import ru.website.micro.videouploadservice.repository.VideoRepository;
import ru.website.micro.videouploadservice.repository.user.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserVideoHelper {
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователь с id:%s не найден.".formatted(userId)));

    }

    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId).orElseThrow(() ->
                new ResourceNotFoundException("Видео с id:%s не найдено.".formatted(videoId)));
    }
}
