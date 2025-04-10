package ru.website.micro.userengagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.website.micro.userengagementservice.exception.ResourceNotFoundException;
import ru.website.micro.userengagementservice.model.Comment;
import ru.website.micro.userengagementservice.repository.CommentRepository;
import org.springframework.data.domain.Pageable;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserVideoHelper userVideoHelper;

    public Comment addComment(UUID userId, Long videoId, String text) {
        Comment comment = Comment.builder()
                .user(userVideoHelper.getUserById(userId))
                .video(userVideoHelper.getVideoById(videoId))
                .text(text)
                .build();

        return commentRepository.save(comment);
    }
    public Comment updateComment(UUID userId, Long commentId, String text) throws AccessDeniedException {

        int isUpdate = commentRepository.updateCommentText(userId,commentId,text);
        if (isUpdate==0) {

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Комментарий не найден"));

            if (!comment.getUser().getId().equals(userId)) {
                throw new AccessDeniedException("Нет прав на редактирование комментария");
            }
            throw new IllegalStateException("Не удалось обновить комментарий");
        }

        return commentRepository.findById(commentId).get();
    }

    public Page<Comment> getUserComments(UUID userId, Long lastCommentId, Pageable pageable) {
        return commentRepository.getCommentsByUserId(userId, lastCommentId, pageable);

    }

    public Page<Comment> getVideoComments(Long videoId, Pageable pageable, Long lastCommentId) {
        return commentRepository.getCommentsByVideoId(videoId, lastCommentId, pageable);
    }

    public void deleteComment(UUID userId, Long commentId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Комментарий c id %s не найден".formatted(commentId)));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("У данного пользователя нет прав на удаление");
        }
        commentRepository.delete(comment);
    }

}
