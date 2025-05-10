package ru.website.micro.userengagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.userengagementservice.model.Comment;
import ru.website.micro.userengagementservice.service.CommentService;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/video/{videoId}")
    public ResponseEntity<Comment> addComment(
            @RequestParam UUID userId,
            @PathVariable Long videoId,
            @RequestBody String text
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(userId, videoId, text));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @RequestParam UUID userId,
            @PathVariable Long commentId,
            @RequestBody String text
    ) throws AccessDeniedException {
        return ResponseEntity.ok(commentService.updateComment(userId, commentId, text));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<Comment>> getUserComments(
            @PathVariable UUID userId,
            @RequestParam(required = false) Long lastCommentId,
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getUserComments(userId, lastCommentId, pageable));
    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<Page<Comment>> getVideoComments(
            @PathVariable Long videoId,
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable,
            @RequestParam(required = false) Long lastCommentId
    ) {
        return ResponseEntity.ok(commentService.getVideoComments(videoId, pageable, lastCommentId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @RequestParam UUID userId,
            @PathVariable Long commentId
    ) throws AccessDeniedException {
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }
}