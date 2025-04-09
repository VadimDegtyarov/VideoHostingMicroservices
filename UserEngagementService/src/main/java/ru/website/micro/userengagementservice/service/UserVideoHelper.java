package ru.website.micro.userengagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.website.micro.userengagementservice.exception.ResourceNotFoundException;
import ru.website.micro.userengagementservice.model.Comment;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.model.user.User;
import ru.website.micro.userengagementservice.repository.CommentRepository;
import ru.website.micro.userengagementservice.repository.VideoRepository;
import ru.website.micro.userengagementservice.repository.user.UserRepository;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserVideoHelper {
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователь с id:%s не найден.".formatted(userId)));

    }

    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId).orElseThrow(() ->
                new ResourceNotFoundException("Видео с id:%s не найдено.".formatted(videoId)));
    }
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Комментарий с id:%s не найден.".formatted(commentId)));
    }
}
